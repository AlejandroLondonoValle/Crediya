package co.com.crediya.model.role.gateways;

import co.com.crediya.model.role.Role;

import java.util.Optional;

public interface RoleRepository {
    // Busca el rol mediante su id
    Optional<Role> findById(Long id);

    // Busca el rol mediante su nombre
    Optional<Role> findByName(String name);
}