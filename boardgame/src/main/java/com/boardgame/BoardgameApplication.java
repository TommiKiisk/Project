package com.boardgame;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.boardgame.model.GameUser;
import com.boardgame.repository.GameUserRepository;

@SpringBootApplication(scanBasePackages = "com.boardgame")
public class BoardgameApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardgameApplication.class, args);
    }

    @Bean
    public CommandLineRunner createDefaultUsers(GameUserRepository gameUserRepository) {
        return (args) -> {
            // Skip creation if there are already users in the database
            if (gameUserRepository.count() > 0) {
                System.out.println("Users already exist in the database. Skipping default user creation.");
                return; 
            }

            

            // Create user 'user' if it doesn't exist
            if (!gameUserRepository.existsByUsername("user")) {
				GameUser user1 = new GameUser("user", "$2a$06$3jYRJrg0ghaaypjZ/.g4SethoeA51ph3UD4kZi9oPkeMTpjKU5uo6", "USER");
				gameUserRepository.save(user1);
				
            }

            // Create user 'admin' if it doesn't exist
            if (!gameUserRepository.existsByUsername("admin")) {
				GameUser user2 = new GameUser("admin", "$2a$10$0MMwY.IQqpsVc1jC8u7IJ.2rT8b0Cd3b3sfIBGV2zfgnPGtT4r0.C", "ADMIN");

                gameUserRepository.save(user2);
            }
        };
    }
}
