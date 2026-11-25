package co.com.crediya.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Datos para registrar un nuevo usuario")
public record RegisterUserRequest(
        @Schema(example = "Alejandro") String firstName,
        @Schema(example = "Londoño") String lastName,
        @Schema(example = "1999-08-15") LocalDate birthDate,
        @Schema(example = "Calle 10 # 20-30, Medellín") String address,
        @Schema(example = "3001234567") String phone,
        @Schema(example = "alejandro@mail.com") String email,
        @Schema(example = "1020304050") String identityDocument,
        @Schema(example = "3500000") BigDecimal baseSalary,
        @Schema(example = "Secreta123*") String password,
        @Schema(description = "Opcional. Si no se envía, el usuario queda con rol CLIENT", example = "3")
        Long roleId) {
}