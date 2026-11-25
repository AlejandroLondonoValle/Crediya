package co.com.crediya.api.user;

import co.com.crediya.api.user.dto.RegisterUserRequest;
import co.com.crediya.api.user.dto.UserResponse;
import co.com.crediya.model.user.User;

public final class UserDtoMapper {

    private UserDtoMapper() {
    }

    public static User toDomain(RegisterUserRequest request) {
        return User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthDate(request.birthDate())
                .address(request.address())
                .phone(request.phone())
                .email(request.email())
                .identityDocument(request.identityDocument())
                .baseSalary(request.baseSalary())
                .password(request.password())
                .roleId(request.roleId())
                .build();
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getIdentityDocument(),
                user.getPhone(),
                user.getBirthDate(),
                user.getAddress(),
                user.getBaseSalary(),
                user.getRoleId());
    }
}