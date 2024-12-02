package com.boardgame.service;

import com.boardgame.model.GameUser;
import com.boardgame.repository.GameUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service  // Ensure this annotation is present
public class GameUserService {

    @Autowired
    private GameUserRepository gameUserRepository;

    public List<GameUser> getAllUsers() {
        return gameUserRepository.findAll();
    }

    public GameUser getUserByUsername(String username) {
        return gameUserRepository.findByUsername(username);
    }

    public GameUser saveUser(GameUser user) {
        return gameUserRepository.save(user);
    }

    public GameUser updateUser(Long userId, GameUser updatedUser) {
        Optional<GameUser> userOptional = gameUserRepository.findById(userId);

        if (userOptional.isPresent()) {
            GameUser existingUser = userOptional.get();
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setRole(updatedUser.getRole());
            existingUser.setPasswordHash(updatedUser.getPasswordHash());

            return gameUserRepository.save(existingUser);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void deleteUser(Long userId) {
        gameUserRepository.deleteById(userId);
    }

    public boolean userExists(String username) {
        return gameUserRepository.existsByUsername(username);
    }

    public void createDefaultUser(String username, String passwordHash, String role) {
        if (!userExists(username)) {
            GameUser newUser = new GameUser(username, passwordHash, role);
            gameUserRepository.save(newUser);
        }
    }
}
