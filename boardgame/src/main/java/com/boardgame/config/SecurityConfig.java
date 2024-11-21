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

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http.csrf().disable() // Disable CSRF for simplicity (adjust for production)
    //         .authorizeHttpRequests()
    //         .requestMatchers("/admin/**").hasRole("ADMIN")  // Only ADMIN role can access /admin
    //         .requestMatchers("/play/**").hasAnyRole("PLAYER", "ADMIN") // PLAYER and ADMIN can access /play
    //         .anyRequest().authenticated()  // All other endpoints require authentication
    //         .and()
    //         .formLogin() // Enable default login page
    //         .permitAll()
    //         .and()
    //         .logout() // Enable logout functionality
    //         .permitAll();
    //     return http.build();
    // }
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		
		http
		.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(antMatcher("/css/**")).permitAll()
				.requestMatchers(antMatcher("/signup")).permitAll()
				.requestMatchers(antMatcher("/saveuser")).permitAll()
				.anyRequest().authenticated()
		)
		.headers(headers -> headers
				.frameOptions(frameoptions -> 
				frameoptions.disable() //for h2 console			
			    )
		)
		.formLogin(formlogin -> formlogin
				.loginPage("/login")
				.defaultSuccessUrl("/home", true)
				.permitAll()
		)
		.logout(logout -> logout
				.permitAll()
		);
		return http.build();
	}
}
