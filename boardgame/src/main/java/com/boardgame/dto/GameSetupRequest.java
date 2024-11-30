package com.boardgame.dto;

import java.util.List;

public class GameSetupRequest {

    private String gameName; // Game name
    private List<String> players;  // List of player names
    private List<String> rules;    // List of rule strings (e.g., "town:3", "city:5")

    // Getters and Setters

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }
}
