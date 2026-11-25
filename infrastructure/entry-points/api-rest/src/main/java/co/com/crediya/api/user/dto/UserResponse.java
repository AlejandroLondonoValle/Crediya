package co.com.crediya.api.user.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String identityDocument,
        String phone,
        LocalDate birthDate,
        String address,
        BigDecimal baseSalary,
        Long roleId) {
}