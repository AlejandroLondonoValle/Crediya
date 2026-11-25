package co.com.crediya.model.user.gateways;

// Puerto de cifrado de contraseñas
public interface PasswordEncoderGateway {
    String encode(String rawPassword);
}