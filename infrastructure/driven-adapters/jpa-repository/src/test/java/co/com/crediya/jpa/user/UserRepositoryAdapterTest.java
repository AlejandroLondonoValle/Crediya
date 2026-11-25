package co.com.crediya.jpa.user;

import co.com.crediya.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;
    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    void shouldSaveUserAndReturnItWithGeneratedId() {
        User user = User.builder()
                .firstName("Ana")
                .lastName("Gómez")
                .email("ana@mail.com")
                .identityDocument("1020304050")
                .birthDate(LocalDate.of(1995, 5, 20))
                .baseSalary(new BigDecimal("2500000"))
                .password("hashed")
                .roleId(3L)
                .build();
        when(userJpaRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        User savedUser = userRepositoryAdapter.save(user);

        assertEquals(1L, savedUser.getId());
        assertEquals("ana@mail.com", savedUser.getEmail());
        assertEquals(new BigDecimal("2500000"), savedUser.getBaseSalary());
        assertEquals(3L, savedUser.getRoleId());
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        when(userJpaRepository.existsByEmail("ana@mail.com")).thenReturn(true);

        assertTrue(userRepositoryAdapter.existsByEmail("ana@mail.com"));
    }

    @Test
    void shouldReturnFalseWhenIdentityDocumentDoesNotExist() {
        when(userJpaRepository.existsByIdentityDocument("999")).thenReturn(false);

        assertFalse(userRepositoryAdapter.existsByIdentityDocument("999"));
    }
}