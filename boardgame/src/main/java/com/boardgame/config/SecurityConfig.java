package com.boardgame.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.boardgame.controller.UserDetailServiceImp;
import com.boardgame.model.GameUser;
import com.boardgame.repository.GameUserRepository;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
	private UserDetailServiceImp userDetailsService;
    private final GameUserRepository userRepository;

    public SecurityConfig(GameUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            
            GameUser user = userRepository.findByUsername(username);
            
            
            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }
            
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    "ADMIN".equals(user.getRole())
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
                .requestMatchers(antMatcher("/game/**")).hasAnyRole("PLAYER", "ADMIN")  // Protect play routes, require player or admin role
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
    @Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}
}
