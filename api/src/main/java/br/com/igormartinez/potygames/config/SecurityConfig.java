package br.com.igormartinez.potygames.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import br.com.igormartinez.potygames.security.jwt.JwtTokenProvider;
import br.com.igormartinez.potygames.security.PasswordManager;
import br.com.igormartinez.potygames.security.jwt.JwtConfigurer;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordManager passwordManager;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return passwordManager.getDefaultPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(HttpBasicConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(
                authorizeHttpRequests -> authorizeHttpRequests
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/auth/signin", "/auth/refresh").permitAll()
                    .requestMatchers("/api/user/v*/signup").permitAll()
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().denyAll()
            )
            /** .exceptionHandling(exceptionHandler -> exceptionHandler
                .accessDeniedHandler(new CustomSpringSecurityExceptionHandler())
                .authenticationEntryPoint(new CustomSpringSecurityExceptionHandler())
            ) */
            .apply(new JwtConfigurer(tokenProvider));
        
        return http.build();
    }
}
