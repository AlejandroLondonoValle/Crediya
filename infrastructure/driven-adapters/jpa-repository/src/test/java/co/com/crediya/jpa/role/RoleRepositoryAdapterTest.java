package co.com.crediya.jpa.role;

import co.com.crediya.model.role.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryAdapterTest {

    private static final RoleEntity CLIENT_ROLE =
            RoleEntity.builder().id(3L).name("CLIENT").description("Cliente").build();

    @Mock
    private RoleJpaRepository roleJpaRepository;
    @InjectMocks
    private RoleRepositoryAdapter roleRepositoryAdapter;

    @Test
    void shouldFindRoleById() {
        when(roleJpaRepository.findById(3L)).thenReturn(Optional.of(CLIENT_ROLE));

        Optional<Role> role = roleRepositoryAdapter.findById(3L);

        assertTrue(role.isPresent());
        assertEquals("CLIENT", role.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenRoleIdDoesNotExist() {
        when(roleJpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(roleRepositoryAdapter.findById(99L).isEmpty());
    }

    @Test
    void shouldFindRoleByName() {
        when(roleJpaRepository.findByName("CLIENT")).thenReturn(Optional.of(CLIENT_ROLE));

        assertEquals(3L, roleRepositoryAdapter.findByName("CLIENT").orElseThrow().getId());
    }
}