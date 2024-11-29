

package com.boardgame;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PasswordHashTest {

    @Test
    public void testPasswordHashing() {
        // The raw password we want to validate
        String rawPassword = "admin";

        // The stored hash from the database (this is just an example hash)
        String storedHash = "$2a$10$erUSHdxbv1JUhZJUlXGQYen.JVhFmDp/.6pF4SETZk3P2YVZXzy7e";

        // Create a BCryptPasswordEncoder instance to validate the password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Validate that the raw password matches the stored hash
        boolean matches = encoder.matches(rawPassword, storedHash);

        // Print the result for debugging purposes
        System.out.println("Password matches: " + matches);

        // Use assertions to verify the result
        assertTrue(matches, "Password should match the stored hash");

        // Optionally, you can also test an incorrect password
        String incorrectPassword = "wrongpassword";
        boolean incorrectMatches = encoder.matches(incorrectPassword, storedHash);
        assertFalse(incorrectMatches, "Incorrect password should not match the stored hash");
    }
}
