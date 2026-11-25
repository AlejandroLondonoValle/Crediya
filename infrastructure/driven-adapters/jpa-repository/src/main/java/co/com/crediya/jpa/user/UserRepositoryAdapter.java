package co.com.crediya.jpa.user;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public User save(User user) {
        log.debug("Persisting new user with roleId={}", user.getRoleId());
        UserEntity savedEntity = userJpaRepository.save(UserEntityMapper.toEntity(user));
        log.info("User persisted successfully with id={}", savedEntity.getId());
        return UserEntityMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdentityDocument(String identityDocument) {
        return userJpaRepository.existsByIdentityDocument(identityDocument);
    }
}