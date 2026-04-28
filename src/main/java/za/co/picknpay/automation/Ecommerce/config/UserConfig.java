package za.co.picknpay.automation.Ecommerce.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        // Hardcoding the registered OMS user into the system memory
        UserDetails admin = User.builder()
                .username("thapelo_oms")
                .password(encoder.encode("OMS_Secure_2026")) // Pre-registered password
                .roles("WAREHOUSE_USER")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}
