package co.com.crediya.api.user;

import co.com.crediya.api.exception.GlobalExceptionHandler;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.registeruser.RegisterUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String USERS_PATH = "/api/v1/usuarios";
    private static final String VALID_BODY = """
            {
              "firstName": "Alejandro",
              "lastName": "Londoño",
              "birthDate": "1999-08-15",
              "address": "Calle 10 # 20-30",
              "phone": "3001234567",
              "email": "alejandro@mail.com",
              "identityDocument": "1020304050",
              "baseSalary": 3500000,
              "password": "Secreta123*"
            }
            """;

    @Mock
    private RegisterUserUseCase registerUserUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(registerUserUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnCreatedWithoutPasswordWhenUserIsRegistered() throws Exception {
        User registeredUser = User.builder()
                .id(1L)
                .email("alejandro@mail.com")
                .baseSalary(new BigDecimal("3500000"))
                .password("hashed")
                .roleId(3L)
                .build();
        when(registerUserUseCase.register(any(User.class))).thenReturn(registeredUser);

        mockMvc.perform(post(USERS_PATH).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("alejandro@mail.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldReturnConflictWhenEmailIsAlreadyRegistered() throws Exception {
        when(registerUserUseCase.register(any(User.class)))
                .thenThrow(new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED));

        mockMvc.perform(post(USERS_PATH).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_REGISTERED"));
    }

    @Test
    void shouldReturnBadRequestWhenBusinessValidationFails() throws Exception {
        when(registerUserUseCase.register(any(User.class)))
                .thenThrow(new BusinessException(ErrorCode.REQUIRED_FIELD, "firstName"));

        mockMvc.perform(post(USERS_PATH).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El campo firstName es obligatorio"));
    }

    @Test
    void shouldReturnBadRequestWhenBaseSalaryIsNotNumeric() throws Exception {
        String bodyWithInvalidSalary = VALID_BODY.replace("3500000", "\"abc\"");

        mockMvc.perform(post(USERS_PATH).contentType(MediaType.APPLICATION_JSON).content(bodyWithInvalidSalary))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
    }

    @Test
    void shouldReturnGenericMessageWhenUnexpectedErrorOccurs() throws Exception {
        when(registerUserUseCase.register(any(User.class))).thenThrow(new IllegalStateException("db down"));

        mockMvc.perform(post(USERS_PATH).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value(
                        "Ocurrió un error inesperado, por favor intenta de nuevo más tarde"));
    }
}