
package com.boardgame.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.boardgame.model.GameUser;
import com.boardgame.repository.GameUserRepository;

@Service
public class GameUserService {

    private final GameUserRepository gameUserRepository;
    
    private final PasswordEncoder passwordEncoder;

    public GameUserService(PasswordEncoder passwordEncoder, GameUserRepository gameUserRepository) {
        this.passwordEncoder = passwordEncoder;
        this.gameUserRepository = gameUserRepository;
    }



    // Validate the password entered by the user
    public boolean validatePassword(String rawPassword, String storedPasswordHash) {
        return passwordEncoder.matches(rawPassword, storedPasswordHash);
    }

    // Example method to find user by username
    public GameUser findByUsername(String username) {
        return gameUserRepository.findByUsername(username);
    }

}
