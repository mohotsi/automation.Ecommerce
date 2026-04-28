package za.co.picknpay.automation.Ecommerce.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import za.co.picknpay.automation.Ecommerce.security.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 1. Disable CSRF for API-level interaction
                .csrf(csrf -> csrf.disable())

                // 2. Set Session Management to Stateless (Standard for JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Configure Authorization Rules
                .authorizeHttpRequests(auth -> auth
                        // Public Endpoints
                        .requestMatchers("/api/oms/login").permitAll()    // To get Bearer Token
                        .requestMatchers("/api/oms/receive").permitAll()  // Playwright Bridge
                        .requestMatchers("/h2-console/**").permitAll()    // Database View

                        // Secured Endpoints (Picking, Packing, Shipping)
                        .requestMatchers("/api/oms/tasks/**").authenticated()

                        // Default
                        .anyRequest().authenticated()
                )

                // 4. Handle H2-Console Frames (allows the UI to display)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // 5. Add the JWT Filter before the standard Username/Password Filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}