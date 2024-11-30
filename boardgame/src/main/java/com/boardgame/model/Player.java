package com.boardgame.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int points = 0;

    private int turnCount = 0; // For tracking turns taken

    // Assuming a Player belongs to a GameSession
    @ManyToOne // Establishing a relationship to GameSession
    private GameSession gameSession; 

    // Getters and Setters
    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public GameSession getGameSession() {
        return gameSession;  // Return the associated game session
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;  // Set the associated game session
    }
}
