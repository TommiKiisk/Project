package com.boardgame.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import com.boardgame.controller.UserDetailServiceImpl;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private UserDetailServiceImpl userDetailsService;

    // Password encoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security filter chain configuration
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/signup").permitAll()
                .requestMatchers("/saveuser").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers("/adduser").hasRole("ADMIN")
                .anyRequest().authenticated()  // Require authentication for any other request
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login")  // Custom login page
                .defaultSuccessUrl("/home", true)  // Redirect to /home after login
                .permitAll()  // Allow everyone to access the login page
            )
            .logout(logout -> logout
                .permitAll()  // Allow everyone to log out
            );
    
        return http.build();
    }
    
    
    

    // Manually configure DaoAuthenticationProvider
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.authenticationProvider(provider);

        return auth.build();
    }
}
