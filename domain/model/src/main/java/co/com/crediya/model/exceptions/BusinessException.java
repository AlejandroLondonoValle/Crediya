package co.com.crediya.model.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessageTemplate(), args));
        this.errorCode = errorCode;
    }
}