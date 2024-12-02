package com.boardgame.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.boardgame.model.GameSession;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {



    // Retrieve the most recent active game session
    Optional<GameSession> findFirstByActiveTrueOrderByStartTimeDesc();



    static Optional<GameSession> getGameSessionById(Long gameSessionId, GameSessionRepository repository) {
        if (gameSessionId == null) {
            throw new IllegalArgumentException("Game session ID cannot be null");
        }
        return repository.findById(gameSessionId);
    }
}

