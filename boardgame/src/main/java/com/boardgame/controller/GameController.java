package com.boardgame.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;              // For REST API responses
import org.springframework.ui.Model;                        // To pass data to the view templates
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;     // To access authenticated user details
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;   // Represents the authenticated user
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;            // To define this class as a Spring MVC controller

import com.boardgame.dto.GameSetupRequest;
import com.boardgame.model.GameConfig;                       // Your domain model for GameConfig
import com.boardgame.model.GameSession;                      // Your domain model for GameSession
import com.boardgame.model.Player;

import com.boardgame.service.GameConfigService;
import com.boardgame.service.GameSessionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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



    @GetMapping("/setup")
    public String showGameSetupPage(Model model) {
        model.addAttribute("gameSetupRequest", new GameSetupRequest());
        return "setup";  // This will return setup.html (Thymeleaf template)
    }

    @PostMapping("/setup")
    public String handleGameSetup(@ModelAttribute GameSetupRequest gameSetupRequest, Model model) {
        try {
            // Assuming 'players' is already a List of strings, so no need for splitting.
            List<String> players = gameSetupRequest.getPlayers();

            // Initialize the list of rules as a Map to store name -> points
            Map<String, Integer> parsedRules = new HashMap<>();

            // Parse the rules from the list of strings (e.g., "town:3, city:5")
            for (String ruleString : gameSetupRequest.getRules()) {
                // Split the rule string by colon (:) to separate rule name and points
                String[] ruleParts = ruleString.split(":");

                if (ruleParts.length == 2) {
                    String ruleName = ruleParts[0].trim();  // Rule name (e.g., "town")
                    try {
                        int rulePoints = Integer.parseInt(ruleParts[1].trim());  // Rule points (e.g., 3)
                        parsedRules.put(ruleName, rulePoints); // Store the parsed rule in the map
                    } catch (NumberFormatException e) {
                        // Handle invalid points format (non-integer value)
                        model.addAttribute("error", "Invalid points format for rule: " + ruleString);
                        return "setup"; // Return to the setup page with an error message
                    }
                } else {
                    // Handle invalid rule input (optional)
                    model.addAttribute("error", "Invalid rule format: " + ruleString);
                    return "setup";  // Return to the setup page with an error message
                }
            }

            // Set the players and parsed rules to the gameSetupRequest
            gameSetupRequest.setPlayers(players);
            // We could set the parsed rules back into the gameSetupRequest if needed,
            // but we're using a Map, so we don't need to convert it to a List.

            // Add the gameSetupRequest to the model to be displayed on the confirmation page
            model.addAttribute("gameSetupRequest", gameSetupRequest);
            model.addAttribute("parsedRules", parsedRules); // Add parsed rules to model

            // Return the success page
            return "gameSuccess";  // Return the view after successful processing
        } catch (Exception e) {
            // Log and handle any errors that may occur during processing
            e.printStackTrace();
            model.addAttribute("error", "There was an error processing the setup.");
            return "setup";  // Return to the setup page with error message
        }
    }

    
    
    @GetMapping("/game")
    public String game() {
        return "game";
    }
    

    @PostMapping("/game-configs")
    public ResponseEntity<GameConfig> createGameConfig(@RequestBody GameConfig config) {
        return ResponseEntity.ok(gameConfigService.saveGameConfig(config));
    }

    @GetMapping("/game-configs")
    public ResponseEntity<Object> getAllGameConfigs() {
        return ResponseEntity.ok(gameConfigService.getAllGameConfigs());
    }

    // Game Session Management
    @PostMapping("/game-sessions")
    public ResponseEntity<GameSession> createGameSession(@RequestParam String gameName, @RequestBody List<Player> players) {
        return ResponseEntity.ok(gameSessionService.createGameSession(gameName, players));
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

    // Player Management
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
