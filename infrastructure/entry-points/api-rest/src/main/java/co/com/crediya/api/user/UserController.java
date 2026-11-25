package co.com.crediya.api.user;

import co.com.crediya.api.exception.ApiErrorResponse;
import co.com.crediya.api.user.dto.RegisterUserRequest;
import co.com.crediya.api.user.dto.UserResponse;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.registeruser.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Registro de usuarios de CrediYa")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registrar un nuevo usuario",
            description = "Registra un usuario validando campos obligatorios, formato y unicidad del correo y documento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o mal formados",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Correo o documento ya registrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterUserRequest request) {
        log.info("Received request to register a new user");
        User registeredUser = registerUserUseCase.register(UserDtoMapper.toDomain(request));
        log.info("User registered successfully with id={}", registeredUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDtoMapper.toResponse(registeredUser));
    }
}