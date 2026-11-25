package co.com.crediya.usecase.registeruser;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.role.Role;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.PasswordEncoderGateway;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    private static final Long ADVISOR_ROLE_ID = 2L;
    private static final Long CLIENT_ROLE_ID = 3L;
    private static final String ENCODED_PASSWORD = "encoded-password";

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoderGateway passwordEncoder;
    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = User.builder()
                .firstName("Alejandro")
                .lastName("Londoño")
                .email("  Alejandro@Mail.com ")
                .identityDocument("1234567890")
                .phone("3001234567")
                .birthDate(LocalDate.of(2000, 1, 15))
                .address("Calle 10 # 20-30")
                .baseSalary(new BigDecimal("3500000"))
                .password("secret123")
                .build();
    }

    private void givenUniqueUser() {
        when(userRepository.existsByEmail("alejandro@mail.com")).thenReturn(false);
        when(userRepository.existsByIdentityDocument("1234567890")).thenReturn(false);
    }

    @Test
    void shouldRegisterUserWithRequestedRole() {
        User userWithRole = validUser.toBuilder().roleId(ADVISOR_ROLE_ID).build();
        givenUniqueUser();
        when(roleRepository.findById(ADVISOR_ROLE_ID))
                .thenReturn(Optional.of(Role.builder().id(ADVISOR_ROLE_ID).name("ADVISOR").build()));
        when(passwordEncoder.encode("secret123")).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = registerUserUseCase.register(userWithRole);

        assertEquals(ADVISOR_ROLE_ID, result.getRoleId());
        assertEquals("alejandro@mail.com", result.getEmail());
        assertEquals(ENCODED_PASSWORD, result.getPassword());
    }

    @Test
    void shouldAssignClientRoleWhenRoleIdIsNotProvided() {
        givenUniqueUser();
        when(roleRepository.findByName("CLIENT"))
                .thenReturn(Optional.of(Role.builder().id(CLIENT_ROLE_ID).name("CLIENT").build()));
        when(passwordEncoder.encode("secret123")).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        registerUserUseCase.register(validUser);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(CLIENT_ROLE_ID, captor.getValue().getRoleId());
    }

    @Test
    void shouldFailWhenRequiredFieldIsMissing() {
        User userWithoutName = validUser.toBuilder().firstName("  ").build();

        BusinessException exception = assertThrows(BusinessException.class,
                () -> registerUserUseCase.register(userWithoutName));

        assertEquals(ErrorCode.REQUIRED_FIELD, exception.getErrorCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenEmailFormatIsInvalid() {
        User userWithBadEmail = validUser.toBuilder().email("correo-invalido").build();

        BusinessException exception = assertThrows(BusinessException.class,
                () -> registerUserUseCase.register(userWithBadEmail));

        assertEquals(ErrorCode.INVALID_EMAIL_FORMAT, exception.getErrorCode());
    }

    @Test
    void shouldFailWhenBaseSalaryIsAboveMaximum() {
        User userWithHighSalary = validUser.toBuilder().baseSalary(new BigDecimal("15000001")).build();

        BusinessException exception = assertThrows(BusinessException.class,
                () -> registerUserUseCase.register(userWithHighSalary));

        assertEquals(ErrorCode.INVALID_BASE_SALARY, exception.getErrorCode());
    }

    @Test
    void shouldFailWhenBaseSalaryIsNegative() {
        User userWithNegativeSalary = validUser.toBuilder().baseSalary(new BigDecimal("-1")).build();

        BusinessException exception = assertThrows(BusinessException.class,
                () -> registerUserUseCase.register(userWithNegativeSalary));

        assertEquals(ErrorCode.INVALID_BASE_SALARY, exception.getErrorCode());
    }

    @Test
    void shouldFailWhenEmailIsAlreadyRegistered() {
        when(userRepository.existsByEmail("alejandro@mail.com")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> registerUserUseCase.register(validUser));

        assertEquals(ErrorCode.EMAIL_ALREADY_REGISTERED, exception.getErrorCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenIdentityDocumentIsAlreadyRegistered() {
        when(userRepository.existsByEmail("alejandro@mail.com")).thenReturn(false);
        when(userRepository.existsByIdentityDocument("1234567890")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> registerUserUseCase.register(validUser));

        assertEquals(ErrorCode.IDENTITY_DOCUMENT_ALREADY_REGISTERED, exception.getErrorCode());
    }

    @Test
    void shouldFailWhenRequestedRoleDoesNotExist() {
        User userWithUnknownRole = validUser.toBuilder().roleId(99L).build();
        givenUniqueUser();
        when(passwordEncoder.encode("secret123")).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> registerUserUseCase.register(userWithUnknownRole));

        assertEquals(ErrorCode.ROLE_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, never()).save(any());
    }
}