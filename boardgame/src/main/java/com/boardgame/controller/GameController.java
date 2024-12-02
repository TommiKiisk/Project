package com.boardgame.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;

import com.boardgame.dto.GameSetupRequest;
import com.boardgame.model.GameSession;
import com.boardgame.model.Player;
import com.boardgame.model.Rule;
import com.boardgame.service.GameSessionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

@Controller
public class GameController {

    private final GameSessionService gameSessionService;

    public GameController(GameSessionService gameSessionService) {
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
        List<GameSession> gameSessions = gameSessionService.getAllGameSessions(); // Fetch all existing game sessions
        model.addAttribute("gameSessions", gameSessions); // Add the list to the model
        model.addAttribute("gameSetupRequest", new GameSetupRequest()); // Initialize an empty request
        return "setup"; // The setup page will display the form
    }

    @GetMapping("/gameSessions")
    public String showGameSessions(Model model) {
        List<GameSession> gameSessions = gameSessionService.getAllGameSessions();

        System.out.println("Players in game sessions:");
        for (GameSession session : gameSessions) {
            System.out.println("Game Session ID: " + session.getId() + ", Game Name: " + session.getGameName());
            if (session.getPlayers() != null) {
                for (Player player : session.getPlayers()) {
                    System.out.println("Player ID: " + player.getId() + ", Name: " + player.getName() + ", Score: " + player.getScore());
                }
            } else {
                System.out.println("No players in this session.");
            }
        }

        model.addAttribute("gameSessions", gameSessions);
        return "gameSessionsPage"; // Ensure this matches the correct Thymeleaf template
    }

    @GetMapping("/game-sessions/{sessionId}/play")
    public String showGameSessionPage(@PathVariable Long sessionId, Model model) {
        GameSession gameSession = gameSessionService.findById(sessionId);
        if (gameSession == null) {
            // Trigger the error page when the session is not found
            throw new RuntimeException("Game session with ID " + sessionId + " not found.");
        }
        
        model.addAttribute("gameSession", gameSession);
        return "play";
    }
    
    @GetMapping("/play/{sessionId}")
    public String showPlayPage(@PathVariable Long sessionId, Model model) {
        GameSession gameSession = gameSessionService.findById(sessionId);
        if (gameSession == null) {
            model.addAttribute("error", "Game session not found.");
            return "error"; // You can customize error page if needed
        }
        model.addAttribute("gameSession", gameSession);
        return "play";
    }

    @PostMapping("/game-sessions/{sessionId}/update-scores")
    public String updateScores(@PathVariable Long sessionId, @RequestParam Map<String, String> scores, Model model) {
        try {
            GameSession gameSession = gameSessionService.findById(sessionId);
            if (gameSession == null) {
                model.addAttribute("error", "Game session not found.");
                return "error";
            }

            // Process the score updates
            for (Map.Entry<String, String> entry : scores.entrySet()) {
                try {
                    Long playerId = Long.parseLong(entry.getKey());  // Parse player ID
                    Integer score = Integer.parseInt(entry.getValue()); // Parse score

                    // Find the player and update their score
                    Player player = gameSessionService.getPlayerById(playerId);
                    if (player != null && player.getGameSession().getId().equals(sessionId)) {
                        player.setScore(score);
                        gameSessionService.savePlayer(player);
                    }
                } catch (NumberFormatException e) {
                    model.addAttribute("error", "Invalid score input for player ID: " + entry.getKey());
                    return "error";
                }
            }

            // Instead of redirecting, re-render the "play" page with updated scores
            model.addAttribute("gameSession", gameSession);
            return "play"; // This will re-render the game session page with updated scores

        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while updating scores: " + e.getMessage());
            return "error"; // Or any other error page
        }
    }


    @PostMapping("/setup")
    public String submitGameSetup(@ModelAttribute GameSetupRequest gameSetupRequest, Model model) {
        try {
            GameSession newGameSession = new GameSession();
            newGameSession.setGameName(gameSetupRequest.getGameName());

            if (gameSetupRequest.getPlayers() != null) {
                for (String playerName : gameSetupRequest.getPlayers()) {
                    newGameSession.addPlayer(new Player(playerName, newGameSession));
                }
            }

            if (gameSetupRequest.getRules() != null) {
                for (Rule rule : gameSetupRequest.getRules()) {
                    newGameSession.addRule(rule);
                }
            }

            gameSessionService.saveGameSession(newGameSession);

            model.addAttribute("message", "Game session setup successfully!");
            return "redirect:/setup";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to setup game session: " + e.getMessage());
            return "setup";
        }
    }

    @PostMapping("/game-sessions/{id}/delete")
    public String deleteGameSession(@PathVariable Long id, Model model) {
        try {
            gameSessionService.deleteGameSession(id);
            model.addAttribute("message", "Game session deleted successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete game session: " + e.getMessage());
        }

        return "redirect:/gameSessions";
    }

    @PostMapping("/game-sessions/{sessionId}/end")
    public String endGameSession(@PathVariable Long sessionId) {
        gameSessionService.endGameSession(sessionId);
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        // Log the error if needed
        e.printStackTrace();

        // Set the error message for the view
        model.addAttribute("errorMessage", "An error occurred: " + e.getMessage());

        // Return the error page view
        return "error";
    }
}
