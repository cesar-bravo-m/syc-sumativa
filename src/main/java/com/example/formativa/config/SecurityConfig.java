package com.example.formativa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.example.formativa.backend.Constants;
import com.example.formativa.backend.CustomAuthenticationProvider;
import com.example.formativa.backend.JWTAuthorizationFilter;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationProvider authProvider;

    @Autowired
    private JWTAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/login")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.GET, "/", "/home", "/register", "/css/**", "/images/**").permitAll()
                .requestMatchers(HttpMethod.GET, Constants.LOGIN_URL).permitAll()
                .requestMatchers(HttpMethod.POST, Constants.LOGIN_URL).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout.permitAll())
            .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
