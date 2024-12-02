
package com.boardgame.service;

import com.boardgame.model.Player;
import com.boardgame.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    // Injecting PlayerRepository via constructor
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // Method to retrieve a Player by ID
    public Player getPlayerById(Long playerId) {
        Optional<Player> player = playerRepository.findById(playerId); // Correct usage
        return player.orElseThrow(() -> new IllegalArgumentException("Player not found with ID: " + playerId));
    }
}
