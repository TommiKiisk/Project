package com.boardgame.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boardgame.model.GameConfig;
import com.boardgame.model.GameSession;
import com.boardgame.model.Player;
import com.boardgame.service.GameConfigService;
import com.boardgame.service.GameSessionService;

@RestController
@RequestMapping("/api/games")
public class GameController {
    @Autowired
    private GameConfigService gameConfigService;

    @Autowired
    private GameSessionService gameSessionService;

    @PostMapping("/config")
    public ResponseEntity<GameConfig> createGameConfig(@RequestBody GameConfig config) {
        return ResponseEntity.ok(gameConfigService.saveGameConfig(config));
    }

    @PostMapping("/session")
    public ResponseEntity<GameSession> createGameSession(@RequestParam String gameName, @RequestBody List<Player> players) {
        return ResponseEntity.ok(gameSessionService.createGameSession(gameName, players));
    }

    @PutMapping("/session/{sessionId}/player/{playerId}/points")
    public ResponseEntity<List<Player>> updatePlayerPoints(
            @PathVariable Long sessionId, @PathVariable Long playerId, @RequestParam int points) {
        return ResponseEntity.ok(gameSessionService.updatePlayerPoints(sessionId, playerId, points));
    }

    @PutMapping("/session/{sessionId}/end")
    public ResponseEntity<GameSession> endGameSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(gameSessionService.endGameSession(sessionId));
    }
}
