package com.boardgame.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gameName;

    private LocalDateTime startTime;

    private boolean active; // Active status of the game session

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id") // Ensures the foreign key is correctly mapped
    private List<Rule> rules = new ArrayList<>();

    private boolean ended;

    // Getters and setters for all fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    // Utility method to add a player
    public void addPlayer(Player player) {
        if (player != null) {
            this.players.add(player);
            player.setGameSession(this); // Ensure bidirectional consistency
        }
    }

    // Utility method to add a rule to the game session
    public void addRule(Rule rule) {
        if (rule != null) {
            this.rules.add(rule);
        }
    }

    // Utility method to remove a rule
    public void removeRule(Rule rule) {
        if (rule != null) {
            this.rules.remove(rule);
        }
    }

    public Player getPlayerById(Long id2) {
        // Iterate through the players list and find the player by id
        for (Player player : players) {
            if (player.getId().equals(id2)) {
                return player;
            }
        }
        // Return null if no player with the given id is found
        return null;
    }
}
