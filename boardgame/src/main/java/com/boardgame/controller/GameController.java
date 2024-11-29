package com.boardgame.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;              // For REST API responses
import org.springframework.ui.Model;                        // To pass data to the view templates
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;     // To access authenticated user details
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;   // Represents the authenticated user
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;            // To define this class as a Spring MVC controller

import com.boardgame.model.GameConfig;                       // Your domain model for GameConfig
import com.boardgame.model.GameSession;                      // Your domain model for GameSession
import com.boardgame.model.Player;

import com.boardgame.service.GameConfigService;
import com.boardgame.service.GameSessionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GameController {

    private final GameConfigService gameConfigService;
    private final GameSessionService gameSessionService;

    public GameController(GameConfigService gameConfigService, GameSessionService gameSessionService) {
        this.gameConfigService = gameConfigService;
        this.gameSessionService = gameSessionService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Ensure login.html exists in the templates directory
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Invalidate the session and clear the authentication
        request.getSession().invalidate();
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        // Redirect to the login page after logout
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("role", user.getAuthorities().toString());
        return "home";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("username", user.getUsername());
        return "admin";
    }


    @GetMapping("/game")
    public String gamePage(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("playerName", user.getUsername());
        model.addAttribute("currentScore", 0); // Update if needed
        return "game";
    }

    @PostMapping("/game-configs")
    public ResponseEntity<GameConfig> createGameConfig(@RequestBody GameConfig config) {
        return ResponseEntity.ok(gameConfigService.saveGameConfig(config));
    }

    @PostMapping("/game-sessions")
    public ResponseEntity<GameSession> createGameSession(@RequestParam String gameName, @RequestBody List<Player> players) {
        return ResponseEntity.ok(gameSessionService.createGameSession(gameName, players));
    }

    @PutMapping("/game-sessions/{sessionId}/players/{playerId}/points")
    public ResponseEntity<?> updatePlayerPoints(@PathVariable Long sessionId, @PathVariable Long playerId, @RequestParam int points) {
        try {
            List<Player> updatedPlayers = gameSessionService.updatePlayerPoints(sessionId, playerId, points);
            return ResponseEntity.ok(updatedPlayers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/game-sessions/{sessionId}/end")
    public ResponseEntity<GameSession> endGameSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(gameSessionService.endGameSession(sessionId));
    }
}
