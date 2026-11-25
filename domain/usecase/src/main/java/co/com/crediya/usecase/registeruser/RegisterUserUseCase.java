package co.com.crediya.usecase.registeruser;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.role.Role;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.PasswordEncoderGateway;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private static final String DEFAULT_ROLE_NAME = "CLIENT";
    private static final BigDecimal MIN_BASE_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_BASE_SALARY = new BigDecimal("15000000");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderGateway passwordEncoder;

    public User register(User user) {
        validateRequiredFields(user);
        String normalizedEmail = normalizeEmail(user.getEmail());
        validateFormats(normalizedEmail, user.getBaseSalary());
        validateUniqueness(normalizedEmail, user.getIdentityDocument());

        User userToSave = user.toBuilder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(user.getPassword()))
                .roleId(resolveRoleId(user.getRoleId()))
                .build();

        return userRepository.save(userToSave);
    }

    private void validateRequiredFields(User user) {
        requireText(user.getFirstName(), "firstName");
        requireText(user.getLastName(), "lastName");
        requireText(user.getEmail(), "email");
        requireText(user.getIdentityDocument(), "identityDocument");
        requireText(user.getPassword(), "password");
        if (user.getBaseSalary() == null) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD, "baseSalary");
        }
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD, fieldName);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validateFormats(String email, BigDecimal baseSalary) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
        if (baseSalary.compareTo(MIN_BASE_SALARY) < 0 || baseSalary.compareTo(MAX_BASE_SALARY) > 0) {
            throw new BusinessException(ErrorCode.INVALID_BASE_SALARY);
        }
    }

    private void validateUniqueness(String email, String identityDocument) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }
        if (userRepository.existsByIdentityDocument(identityDocument)) {
            throw new BusinessException(ErrorCode.IDENTITY_DOCUMENT_ALREADY_REGISTERED);
        }
    }

    private Long resolveRoleId(Long requestedRoleId) {
        if (requestedRoleId == null) {
            return roleRepository.findByName(DEFAULT_ROLE_NAME)
                    .map(Role::getId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
        }
        return roleRepository.findById(requestedRoleId)
                .map(Role::getId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
    }
}