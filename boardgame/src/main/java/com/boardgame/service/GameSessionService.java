package com.boardgame.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boardgame.model.GameSession;
import com.boardgame.model.Player;
import com.boardgame.repository.GameSessionRepository;

@Service
public class GameSessionService {
    @Autowired
    private GameSessionRepository gameSessionRepository;

    public GameSession createGameSession(String gameName, List<Player> players) {
        GameSession session = new GameSession();
        session.setGameName(gameName);
        session.setPlayers(players);
        session.setStartTime(LocalDateTime.now());
        return gameSessionRepository.save(session);
    }

    public GameSession endGameSession(Long sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId).orElseThrow();
        session.setEndTime(LocalDateTime.now());
        return gameSessionRepository.save(session);
    }

    public List<Player> updatePlayerPoints(Long sessionId, Long playerId, int points) {
        GameSession session = gameSessionRepository.findById(sessionId).orElseThrow();
        session.getPlayers().stream()
            .filter(player -> player.getId().equals(playerId))
            .findFirst()
            .ifPresent(player -> player.setPoints(player.getPoints() + points));
        return session.getPlayers();
    }
}
