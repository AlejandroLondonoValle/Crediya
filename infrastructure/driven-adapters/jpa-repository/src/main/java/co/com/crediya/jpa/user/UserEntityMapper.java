package co.com.crediya.jpa.user;

import co.com.crediya.model.user.User;

public final class UserEntityMapper {

    private UserEntityMapper() {
    }

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .identityDocument(user.getIdentityDocument())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .baseSalary(user.getBaseSalary())
                .password(user.getPassword())
                .roleId(user.getRoleId())
                .build();
    }

    public static User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .identityDocument(entity.getIdentityDocument())
                .phone(entity.getPhone())
                .birthDate(entity.getBirthDate())
                .address(entity.getAddress())
                .baseSalary(entity.getBaseSalary())
                .password(entity.getPassword())
                .roleId(entity.getRoleId())
                .build();
    }
}