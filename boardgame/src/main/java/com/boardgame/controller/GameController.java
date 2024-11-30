package com.boardgame.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;

import com.boardgame.dto.GameSetupRequest;
import com.boardgame.model.GameConfig;
import com.boardgame.model.GameSession;
import com.boardgame.model.Player;

import com.boardgame.service.GameConfigService;
import com.boardgame.service.GameSessionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

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
        request.getSession().invalidate();
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
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

    @GetMapping("/setup")
    public String showGameSetupPage(Model model) {
        model.addAttribute("gameSetupRequest", new GameSetupRequest());
        return "setup"; // The setup page will display the form
    }

    @PostMapping("/setup")
    public String handleGameSetup(@ModelAttribute GameSetupRequest gameSetupRequest, Model model) {
        try {
            List<String> players = gameSetupRequest.getPlayers();
            Map<String, Integer> parsedRules = new HashMap<>();

            // Process the rules
            for (String ruleString : gameSetupRequest.getRules()) {
                String[] ruleParts = ruleString.split(":");
                
                // Check if rule format is valid
                if (ruleParts.length == 2) {
                    String ruleName = ruleParts[0].trim();
                    try {
                        int rulePoints = Integer.parseInt(ruleParts[1].trim()); // Parse points as an integer
                        parsedRules.put(ruleName, rulePoints); // Add rule to map
                    } catch (NumberFormatException e) {
                        model.addAttribute("error", "Invalid points format for rule: " + ruleString);
                        return "setup"; // Return to setup page with error message
                    }
                } else {
                    model.addAttribute("error", "Invalid rule format: " + ruleString);
                    return "setup"; // Return to setup page with error message
                }
            }

            // Add the parsed rules and game setup request to the model for the next page
            model.addAttribute("gameSetupRequest", gameSetupRequest);
            model.addAttribute("parsedRules", parsedRules);

            // Redirect to a success page after setup
            return "gameSuccess"; // This page will show the game setup results

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "There was an error processing the setup.");
            return "setup"; // Return to setup page with error message
        }
    }

    @GetMapping("/play")
    public String showGameSessionPage(Model model) {
        // Assuming you have a method to get the current game session
        GameSession gameSession = gameSessionService.getCurrentGameSession();
    
        if (gameSession == null) {
            model.addAttribute("error", "Game session not found.");
            return "error";  // Or wherever you want to redirect if no session exists
        }
    
        model.addAttribute("gameSession", gameSession);
        model.addAttribute("players", gameSession.getPlayers());  // Add players as well if needed
        return "play";  // This will render play.html
    }

    @PostMapping("/game/update-scores")
    public String updateScores(@RequestParam Map<String, String> scores, Model model) {
        try {
            for (Map.Entry<String, String> entry : scores.entrySet()) {
                String playerName = entry.getKey();
                int pointsToAdd = Integer.parseInt(entry.getValue());

                Player player = gameSessionService.findPlayerByName(playerName);
                if (player != null) {
                    int updatedPoints = player.getPoints() + pointsToAdd;
                    gameSessionService.updatePlayerPoints(player.getGameSession().getId(), player.getId(), updatedPoints);
                }
            }

            List<Player> players = gameSessionService.getAllPlayers();
            model.addAttribute("players", players);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update scores: " + e.getMessage());
            e.printStackTrace();
        }

        return "play";
    }

    @PostMapping("/game-configs")
    public ResponseEntity<GameConfig> createGameConfig(@RequestBody GameConfig config) {
        return ResponseEntity.ok(gameConfigService.saveGameConfig(config));
    }

    @GetMapping("/game-configs")
    public ResponseEntity<Object> getAllGameConfigs() {
        return ResponseEntity.ok(gameConfigService.getAllGameConfigs());
    }

    @PostMapping("/game-sessions")
    public ResponseEntity<GameSession> createGameSession(@RequestParam String gameName, @RequestBody List<Player> players) {
        return ResponseEntity.ok(gameSessionService.createGameSession(gameName, players));
    }

    @PostMapping("/game-sessions/{sessionId}/players/{playerId}/update-score")
    public String updatePlayerScore(
            @PathVariable Long sessionId,
            @PathVariable Long playerId,
            @RequestParam int score,
            Model model) {
        try {
            // Update the player's score
            gameSessionService.updatePlayerPoints(sessionId, playerId, score);

            // Reload the game session players
            return "redirect:/game-sessions/" + sessionId + "/play";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update score: " + e.getMessage());
            return "play";
        }
    }
    @GetMapping("/game-sessions/{sessionId}/play")
    public String showGameSessionPlayers(@PathVariable Long sessionId, Model model) {
        try {
            GameSession session = gameSessionService.getGameSessionById(sessionId);
            if (session == null) {
                throw new IllegalArgumentException("Game session not found");
            }
            model.addAttribute("gameSession", session);
            model.addAttribute("players", session.getPlayers());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load the game session: " + e.getMessage());
            return "error"; // Point to a generic error page
        }
        return "play";
    }


    @PutMapping("/game-sessions/{sessionId}/players/{playerId}/points")
    public ResponseEntity<?> updatePlayerPoints(
            @PathVariable Long sessionId,
            @PathVariable Long playerId,
            @RequestParam int points) {
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

    @PostMapping("/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        return ResponseEntity.ok(gameSessionService.savePlayer(player));
    }

    @GetMapping("/players")
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(gameSessionService.getAllPlayers());
    }

    @GetMapping("/players/{playerId}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long playerId) {
        return ResponseEntity.ok(gameSessionService.getPlayerById(playerId));
    }
}
