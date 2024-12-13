package be.pxl.activity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable(); // Disable CSRF for testing/development
        http.headers().frameOptions().disable(); // Allow H2 console to be displayed in frames

        // Allow unauthenticated access to H2 console
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/h2-console/**").permitAll() // Allow access to the H2 console without authentication
                .requestMatchers(HttpMethod.POST, "/users").permitAll() // Allow public access to POST /users
                .requestMatchers(HttpMethod.GET, "/health").permitAll() // Allow public access to health check
                .anyRequest().authenticated() // Require authentication for all other requests
        );

        http.httpBasic(); // Enable Basic Authentication
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Stateless session management

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
