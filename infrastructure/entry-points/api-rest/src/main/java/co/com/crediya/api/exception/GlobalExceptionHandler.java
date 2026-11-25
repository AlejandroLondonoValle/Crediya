package co.com.crediya.api.exception;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String MALFORMED_REQUEST_CODE = "MALFORMED_REQUEST";
    private static final String MALFORMED_REQUEST_MESSAGE =
            "El cuerpo de la petición es inválido o algún campo tiene un formato incorrecto";
    private static final String DATA_CONFLICT_CODE = "DATA_CONFLICT";
    private static final String DATA_CONFLICT_MESSAGE = "Ya existe un registro con los datos proporcionados";
    private static final String HTTP_ERROR_CODE_PREFIX = "HTTP_";
    private static final String HTTP_ERROR_MESSAGE = "La petición no pudo ser procesada";
    private static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";
    private static final String INTERNAL_ERROR_MESSAGE =
            "Ocurrió un error inesperado, por favor intenta de nuevo más tarde";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex,
                                                                    HttpServletRequest request) {
        log.warn("Business rule violated: code={}, path={}", ex.getErrorCode(), request.getRequestURI());
        return buildResponse(resolveStatus(ex.getErrorCode()), ex.getErrorCode().name(), ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedRequest(HttpMessageNotReadableException ex,
                                                                   HttpServletRequest request) {
        log.warn("Malformed request body: path={}, detail={}", request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, MALFORMED_REQUEST_CODE, MALFORMED_REQUEST_MESSAGE, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                         HttpServletRequest request) {
        log.warn("Data integrity violation: path={}", request.getRequestURI());
        return buildResponse(HttpStatus.CONFLICT, DATA_CONFLICT_CODE, DATA_CONFLICT_MESSAGE, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        if (ex instanceof ErrorResponse springError) {
            HttpStatusCode status = springError.getStatusCode();
            log.warn("HTTP error {} on path={}", status.value(), request.getRequestURI());
            return buildResponse(status, HTTP_ERROR_CODE_PREFIX + status.value(), HTTP_ERROR_MESSAGE, request);
        }
        log.error("Unexpected error processing path={}", request.getRequestURI(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR_CODE, INTERNAL_ERROR_MESSAGE, request);
    }

    private HttpStatus resolveStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case EMAIL_ALREADY_REGISTERED, IDENTITY_DOCUMENT_ALREADY_REGISTERED -> HttpStatus.CONFLICT;
            case REQUIRED_FIELD, INVALID_EMAIL_FORMAT, INVALID_BASE_SALARY, ROLE_NOT_FOUND -> HttpStatus.BAD_REQUEST;
        };
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatusCode status, String code, String message,
                                                           HttpServletRequest request) {
        return ResponseEntity.status(status).body(ApiErrorResponse.of(code, message, request.getRequestURI()));
    }
}