package com.boardgame;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.boardgame.model.GameUser;
import com.boardgame.repository.GameUserRepository;

@SpringBootApplication
public class BoardgameApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardgameApplication.class, args);
	}

	@Bean
	public CommandLineRunner bookDemo( GameUserRepository grepository) {
		return (args) -> {
			
			
			

			if (!grepository.existsByUsername("user")) {
				GameUser user1 = new GameUser("user", "$2a$06$3jYRJrg0ghaaypjZ/.g4SethoeA51ph3UD4kZi9oPkeMTpjKU5uo6", "USER");
				
				grepository.save(user1);
				
			}
			

			if (!grepository.existsByUsername("admin")) {
				GameUser user2 = new GameUser("admin", "$2a$10$0MMwY.IQqpsVc1jC8u7IJ.2rT8b0Cd3b3sfIBGV2zfgnPGtT4r0.C", "ADMIN");
				grepository.save(user2);
			}
			// log.info("fetch all books");
			// for (Book book : brepository.findAll()) {
			// 	log.info(book.toString());
			// }
		};
	}
}
