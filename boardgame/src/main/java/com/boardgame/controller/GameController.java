package com.boardgame.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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


import org.springframework.ui.Model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;


@RestController
@RequestMapping("/api/games")
public class GameController {
    @Autowired
    private GameConfigService gameConfigService;

    @Autowired
    private GameSessionService gameSessionService;

    

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("role", user.getAuthorities().toString());
        return "home";
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("username", user.getUsername());
        return "admin";
    }

    @GetMapping("/play")
    public String gamePage(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("playerName", user.getUsername());
        model.addAttribute("currentScore", 0); // Add your game score logic here
        return "game";
    }

    @GetMapping("/logout")
    public String logout() {
        return "login"; // This will automatically log the user out via Spring Security
    }


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
