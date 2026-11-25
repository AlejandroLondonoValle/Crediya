package co.com.crediya.jpa.role;

import co.com.crediya.model.role.Role;
import co.com.crediya.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findById(id).map(RoleEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name).map(RoleEntityMapper::toDomain);
    }
}