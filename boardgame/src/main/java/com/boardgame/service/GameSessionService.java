package com.boardgame.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boardgame.model.GameSession;
import com.boardgame.model.Player;
import com.boardgame.repository.GameSessionRepository;
import com.boardgame.repository.PlayerRepository;

@Service
public class GameSessionService {

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PlayerRepository playerRepository; // Added PlayerRepository for player operations

    /**
     * Creates a new game session with the provided game name and players.
     */
    public GameSession createGameSession(String gameName, List<Player> players) {
        GameSession session = new GameSession();
        session.setGameName(gameName);

        // Persist players before associating them with the session
        players.forEach(playerRepository::save);
        session.setPlayers(players);

        return gameSessionRepository.save(session);
    }

    /**
     * Updates the points of a specific player in a session.
     */
    public List<Player> updatePlayerPoints(Long sessionId, Long playerId, int points) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Game session not found with ID: " + sessionId));

        Player player = session.getPlayers().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player not found with ID: " + playerId));

        player.setPoints(points);

        // Save the updated session and return the updated players
        gameSessionRepository.save(session);
        return session.getPlayers();
    }

    /**
     * Marks a game session as ended.
     */
    public GameSession endGameSession(Long sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Game session not found with ID: " + sessionId));

        session.setEnded(true);

        return gameSessionRepository.save(session);
    }

    /**
     * Saves a new player.
     */
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    /**
     * Retrieves all players from the database.
     */
    public List<Player> getAllPlayers() {
        return (List<Player>) playerRepository.findAll();
    }

    /**
     * Retrieves a specific player by ID.
     */
    public Player getPlayerById(Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found with ID: " + playerId));
    }
}
