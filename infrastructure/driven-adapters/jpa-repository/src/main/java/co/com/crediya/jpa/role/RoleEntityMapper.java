package co.com.crediya.jpa.role;

import co.com.crediya.model.role.Role;

public final class RoleEntityMapper {

    private RoleEntityMapper() {
    }

    public static Role toDomain(RoleEntity entity) {
        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }
}