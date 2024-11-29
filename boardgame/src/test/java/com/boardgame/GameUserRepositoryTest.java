
package com.boardgame;

import com.boardgame.model.GameUser;
import com.boardgame.repository.GameUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class GameUserRepositoryTest {

    @Autowired
    private GameUserRepository gameUserRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String plainPasswordUser = "user";  // The plain password for "user"
    private String plainPasswordAdmin = "admin";  // The plain password for "admin"

    @BeforeEach
    void setUp() {
        // Ensure the test data is set up, this should match what you might already have in your database
        if (!gameUserRepository.existsByUsername("user")) {
            gameUserRepository.save(new GameUser("user", "$2a$10$0MMwY.IQqpsVc1jC8u7IJ.2rT8b0Cd3b3sfIBGV2zfgnPGtT4r0.C", "USER"));
        }
        if (!gameUserRepository.existsByUsername("admin")) {
            gameUserRepository.save(new GameUser("admin", "$2a$10$0MMwY.IQqpsVc1jC8u7IJ.2rT8b0Cd3b3sfIBGV2zfgnPGtT4r0.C", "ADMIN"));
        }
    }

    @Test
    void testPasswordHashingForUser() {
        // Retrieve user from DB
        GameUser user = gameUserRepository.findByUsername("user");

        // Ensure user is found
        assertNotNull(user);

        // Validate that the password matches the stored hash
        boolean passwordMatches = passwordEncoder.matches(plainPasswordUser, user.getPassword());
        
        // Assert that the password matches
        assertTrue(passwordMatches, "Password for 'user' does not match the stored hash.");
    }


    @Test
    void testPasswordHashingForAdmin() {
        // Retrieve admin from DB
        GameUser admin = gameUserRepository.findByUsername("admin");

        // Ensure admin is found
        assertNotNull(admin);

        // Print the stored password hash for debugging
        System.out.println("Stored password hash: " + admin.getPassword());

        // Validate that the password matches the stored hash
        boolean passwordMatches = passwordEncoder.matches(plainPasswordAdmin, admin.getPassword());

        // Print the result of the password match for debugging
        System.out.println("Password matches: " + passwordMatches);

        // Assert that the password matches
        assertTrue(passwordMatches, "Password for 'admin' does not match the stored hash.");
    }

}
