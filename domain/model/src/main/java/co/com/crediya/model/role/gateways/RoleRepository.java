package co.com.crediya.model.role.gateways;

import co.com.crediya.model.role.Role;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findById(Long id);
}