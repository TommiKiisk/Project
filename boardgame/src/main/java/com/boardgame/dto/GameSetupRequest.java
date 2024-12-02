package com.boardgame.dto;

import java.util.ArrayList;
import java.util.List;

import com.boardgame.model.Rule;

public class GameSetupRequest {

    private String gameName;
    private List<String> players = new ArrayList<>();
    private List<Rule> rules = new ArrayList<>();

    // Getters and Setters for gameName, players, and rules
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

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}
