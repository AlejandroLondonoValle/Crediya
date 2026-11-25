package co.com.crediya.passwordencoder;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BCryptPasswordEncoderAdapterTest {

    private final BCryptPasswordEncoderAdapter adapter = new BCryptPasswordEncoderAdapter();

    @Test
    void shouldEncodePasswordSoThatItMatchesTheOriginal() {
        String encoded = adapter.encode("secret123");

        assertNotEquals("secret123", encoded);
        assertTrue(new BCryptPasswordEncoder().matches("secret123", encoded));
    }

    @Test
    void shouldGenerateDifferentHashesForSamePassword() {
        assertNotEquals(adapter.encode("secret123"), adapter.encode("secret123"));
    }
}