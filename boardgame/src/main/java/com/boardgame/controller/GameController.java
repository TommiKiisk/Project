package com.boardgame.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;

import com.boardgame.dto.GameSetupRequest;
import com.boardgame.model.GameSession;
import com.boardgame.model.GameUser;
import com.boardgame.model.Player;
import com.boardgame.model.Rule;
import com.boardgame.repository.GameSessionRepository;
import com.boardgame.service.GameSessionService;
import com.boardgame.service.GameUserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@Controller
public class GameController {

    private final GameSessionService gameSessionService;

    @Autowired
    private GameUserService gameUserService;

    public GameController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
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
    
        // Check if the user has the "ROLE_ADMIN" authority
        if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin";  // Redirect to admin dashboard if the user is an admin
        } else {
            return "home";  // Regular home page for non-admin users
        }
    }
    
    
    
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")  // Use hasAuthority instead of hasRole
    public String adminDashboard(Model model, Authentication authentication) {
        // Admin-specific page
        User user = (User) authentication.getPrincipal();
        model.addAttribute("username", user.getUsername());
        return "admin";  // Render admin page
    }

    // Endpoint to show the "Manage Users" page
    @GetMapping("/admin/manage-users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String manageUsers(Model model, Authentication authentication) {
        // Retrieve users from the database using the service
        List<GameUser> users = gameUserService.getAllUsers();

        // Add the list of users to the model
        model.addAttribute("users", users);
        return "manage-users";  // Return the 'manage-users' view
    }

    // Endpoint to delete a user
    @GetMapping("/delete-user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteUser(@PathVariable Long userId) {
        gameUserService.deleteUser(userId);
        return "redirect:/admin/manage-users";  // Redirect to manage-users after deletion
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

    @Controller
    @RequestMapping("/game-sessions")
    public class GameSessionController {

        private final GameSessionRepository gameSessionRepository;

        public GameSessionController(GameSessionRepository gameSessionRepository) {
            this.gameSessionRepository = gameSessionRepository;
        }

        @GetMapping("/play")
        public String playGame(@RequestParam Long gameSessionId, Model model) {
            Optional<GameSession> gameSession = gameSessionRepository.findById(gameSessionId);
        
            if (gameSession.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid game session ID");
            }
        
            model.addAttribute("gameSession", gameSession.get());
            return "play";
        }
        
    }

    @PostMapping("/game-sessions/{sessionId}/update-scores")
    public String updateScores(@PathVariable Long sessionId, 
                            @ModelAttribute GameSession gameSession, 
                            Model model) {
        try {
            // Fetch the game session by ID
            GameSession gameSessionToUpdate = gameSessionService.getGameSessionById(sessionId);
            
            if (gameSessionToUpdate == null) {
                model.addAttribute("error", "Game session not found.");
                return "error";
            }

            // Add the updated player scores (instead of replacing, we add to existing score)
            if (gameSession.getPlayers() != null) {
                for (Player updatedPlayer : gameSession.getPlayers()) {
                    // Find the corresponding player in the current game session
                    Optional<Player> existingPlayer = gameSessionToUpdate.getPlayers().stream()
                            .filter(player -> player.getId().equals(updatedPlayer.getId()))
                            .findFirst();

                    // If the player exists, add the new score to the existing score
                    if (existingPlayer.isPresent()) {
                        Player playerToUpdate = existingPlayer.get();
                        playerToUpdate.setScore(playerToUpdate.getScore() + updatedPlayer.getScore());
                    } else {
                        // If the player is not found in the session, you can optionally handle this case
                        // For example, you could add the player to the game session or ignore
                        gameSessionToUpdate.addPlayer(updatedPlayer);
                    }
                }
            }

            // Save the updated game session
            gameSessionService.saveGameSession(gameSessionToUpdate);

            // Pass the updated game session to the model
            model.addAttribute("gameSession", gameSessionToUpdate);
            return "play"; // This will re-render the play page with updated scores

        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while updating scores: " + e.getMessage());
            return "error";
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
    public String endGameSession(@PathVariable Long sessionId, Model model) {
        try {
            // Fetch the game session by ID
            GameSession gameSessionToEnd = gameSessionService.getGameSessionById(sessionId);
    
            if (gameSessionToEnd == null) {
                model.addAttribute("error", "Game session not found.");
                return "error";
            }
    
            // Mark the session as ended
            gameSessionService.endGameSession(sessionId);
    
            // Optionally, add a success message
            model.addAttribute("message", "Game session ended successfully!");
    
            // Redirect to a suitable page (home, game sessions list, etc.)
            return "redirect:/";  // Redirect to home page or another appropriate page
    
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while ending the game session: " + e.getMessage());
            return "error";
        }
    }

        private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Endpoint to display the "Add User" form
    @GetMapping("/adduser")
    public String showAddUserForm(Model model) {
        return "adduser";  // Return the adduser.html page
    }

    // Endpoint to handle the form submission and save the new user
    @PostMapping("/admin/save-user")
    public String saveUser(@RequestParam String username, @RequestParam String password, @RequestParam String role) {
        // Encrypt the password before saving
        String encryptedPassword = passwordEncoder.encode(password);

        // Create a new GameUser object
        GameUser newUser = new GameUser(username, encryptedPassword, role);

        // Save the user to the database
        gameUserService.saveUser(newUser);

        // Redirect to the manage-users page after saving the user
        return "redirect:/admin/manage-users";
    }
    
}
