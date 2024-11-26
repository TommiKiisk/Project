package com.boardgame.service;

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
        return gameSessionRepository.save(session);
    }

    public List<Player> updatePlayerPoints(Long sessionId, Long playerId, int points) {
        GameSession session = gameSessionRepository.findById(sessionId).orElseThrow();
        Player player = session.getPlayers().stream()
                               .filter(p -> p.getId().equals(playerId))
                               .findFirst()
                               .orElseThrow();
        player.setPoints(points);
        gameSessionRepository.save(session);
        return session.getPlayers();
    }

    public GameSession endGameSession(Long sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId).orElseThrow();
        session.setEnded(true);
        return gameSessionRepository.save(session);
    }
}

