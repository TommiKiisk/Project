package com.boardgame;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.boardgame.model.GameUser;
import com.boardgame.repository.GameUserRepository;

@SpringBootApplication
public class BoardgameApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardgameApplication.class, args);
    }

    @Bean
    public CommandLineRunner createDefaultUsers(GameUserRepository gameUserRepository) {
        return (args) -> {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // Skip creation if there are already users in the database
            if (gameUserRepository.count() > 0) {
                System.out.println("Users already exist in the database. Skipping default user creation.");
                return; 
            }

            // Create user 'user' if it doesn't exist
            if (!gameUserRepository.existsByUsername("user")) {
                String encodedPassword1 = passwordEncoder.encode("user");  // Encode plain password
                GameUser user1 = new GameUser("user", encodedPassword1, "USER");
                gameUserRepository.save(user1);
            }

            // Create user 'admin' if it doesn't exist
            if (!gameUserRepository.existsByUsername("admin")) {
                String encodedPassword2 = passwordEncoder.encode("admin");  // Encode plain password
                GameUser user2 = new GameUser("admin", encodedPassword2, "ADMIN");
                gameUserRepository.save(user2);
            }
        };
    }
}
