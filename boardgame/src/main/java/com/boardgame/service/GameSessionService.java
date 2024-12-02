package com.boardgame.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boardgame.model.GameSession;
import com.boardgame.model.Player;
import com.boardgame.repository.GameSessionRepository;
import com.boardgame.repository.PlayerRepository;

import jakarta.transaction.Transactional;

@Service
public class GameSessionService {

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public GameSessionService(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    /**
     * Creates a new game session with the provided game name and players.
     */


    public GameSession createGameSession(String gameName, List<Player> players) {
        GameSession session = new GameSession();
        session.setGameName(gameName);
        session.setPlayers(players.stream()
            .peek(player -> player.setScore(0)) // Initialize points to 0
            .toList());
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

        player.setScore(points);

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
        return playerRepository.findAll();
    }

    /**
     * Retrieves a specific player by ID.
     */
    public Player getPlayerById(Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found with ID: " + playerId));
    }

    /**
     * Finds a player by their name (method implementation can be added later).
     */
    public Player findPlayerByName(String playerName) {
        // Replace with actual implementation if necessary
        throw new UnsupportedOperationException("Unimplemented method 'findPlayerByName'");
    }

    public GameSession getGameSessionById(Long sessionId) {
        return gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("GameSession with ID " + sessionId + " not found"));
    }

    public GameSession getCurrentGameSession() {
        // Fetch the most recent active game session
        Optional<GameSession> optionalGameSession = gameSessionRepository.findFirstByActiveTrueOrderByStartTimeDesc();

        if (optionalGameSession.isPresent()) {
            return optionalGameSession.get();  // Return the found game session
        } else {
            // Handle the case where no active game session exists
            GameSession newGameSession = new GameSession();
            newGameSession.setStartTime(LocalDateTime.now());  // Set a start time if creating a new session
            gameSessionRepository.save(newGameSession);
            return newGameSession;
        }
    }

    public void updatePlayerScore(Long playerId, Integer score) {
        Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        player.setScore(score);
        playerRepository.save(player);
    }


    @Transactional
    public List<GameSession> getAllGameSessions() {
        List<GameSession> gameSessions = gameSessionRepository.findAll();
        // Force initialization of players
        gameSessions.forEach(session -> session.getPlayers().size());
        return gameSessions;
    }
    

    public void saveGameSession(GameSession newGameSession) {
        gameSessionRepository.save(newGameSession);
    }

    public GameSession findById(Long sessionId) {
        return gameSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Game session not found with ID: " + sessionId));
    }

    public void deleteGameSession(Long id) {
        // Check if the game session exists before deleting
        GameSession gameSession = gameSessionRepository.findById(id).orElseThrow(() -> new RuntimeException("Game session not found"));
        
        // Delete the game session
        gameSessionRepository.delete(gameSession);
    }
    
}
