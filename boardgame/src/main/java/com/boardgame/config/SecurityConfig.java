package com.boardgame.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.boardgame.model.Role;
import com.boardgame.repository.UserRepository;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.boardgame.model.User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getRole() == Role.ADMIN
                            ? AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_PLAYER")
                            : AuthorityUtils.createAuthorityList("ROLE_PLAYER")
            );
        };
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(antMatcher("/css/**")).permitAll()  // Allow CSS files
                .requestMatchers(antMatcher("/signup")).permitAll()  // Allow signup endpoint
                .requestMatchers(antMatcher("/saveuser")).permitAll()  // Allow saveuser endpoint
                .requestMatchers(antMatcher("/login")).permitAll()  // Allow login page
                .requestMatchers(antMatcher("/home")).authenticated()  // Protect home page, require login
                .requestMatchers(antMatcher("/admin/**")).hasRole("ADMIN")  // Protect admin routes, require admin role
                .requestMatchers(antMatcher("/play/**")).hasAnyRole("PLAYER", "ADMIN")  // Protect play routes, require player or admin role
                .anyRequest().authenticated()  // Protect all other routes, require login
            )
            .headers(headers -> headers
                .frameOptions(frameoptions -> frameoptions.disable())  // Disable frame options for H2 console
            )
            .formLogin(formlogin -> formlogin
                .loginPage("/login")  // Specify the custom login page
                .defaultSuccessUrl("/home", true)  // Redirect to home after successful login
                .permitAll()  // Allow all users to access the login page
            )
            .logout(logout -> logout
                .permitAll()  // Allow all users to log out
            );

        return http.build();
    }
}
