package com.sparta.doom.fantasticninewebandapi.config;

import com.sparta.doom.fantasticninewebandapi.security.api.TokenRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final TokenRequestFilter tokenRequestFilter;

    @Autowired
    public SecurityConfig(TokenRequestFilter tokenRequestFilter) {
        this.tokenRequestFilter = tokenRequestFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authRequest -> authRequest
                        // Public pages
                        .requestMatchers("/", "/welcome/").permitAll()
                        .requestMatchers("/css/**", "/images/**", "/javascript/**").permitAll()
                        .requestMatchers("/**").permitAll()

                        // API
                        .requestMatchers("/api/**").permitAll()

                        // Web
                        .requestMatchers("/movies/create", "/theaters/create", "/users/create", "/schedules/create").hasRole("ADMIN")
                        .requestMatchers("/movies/{id}", "/theaters/{id}", "/users/{id}/").hasRole("ADMIN")
                        .requestMatchers("/movies/{id}/comments/create/").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/movies/{id}/comments/{id}/").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/movies/**", "/theaters/**", "/users/**", "/schedules/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout/")
                        .permitAll()
                );

        http.addFilterBefore(tokenRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // dev chain
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authRequest ->
//                        authRequest.anyRequest().permitAll())
//                .build();
//    }
}
