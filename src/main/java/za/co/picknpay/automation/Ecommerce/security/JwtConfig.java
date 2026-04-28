package za.co.picknpay.automation.Ecommerce.security;


import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.crypto.SecretKey;
import java.util.Base64;

@Configuration
public class JwtConfig {

    /**
     * This creates a proper SecretKey object.
     * For production, you'd load a fixed string, but for your OMS,
     * we can generate a secure one or use a hardcoded secure constant.
     */
    @Bean
    public SecretKey jwtSecretKey() {
        // Option A: Generate a random secure key every time the app starts
        // return Keys.secretKeyFor(SignatureAlgorithm.HS256);

        // Option B: Create it from a secure hardcoded string (More practical for testing)
        String hardcodedSecret = "9a4f2c3d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0t1u2v3w4x5y6z";
        return Keys.hmacShaKeyFor(hardcodedSecret.getBytes());
    }
}
