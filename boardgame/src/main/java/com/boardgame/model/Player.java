package com.boardgame.model;

import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false) // Ensure the name is non-null in the database
    private String name;

    @Column(name = "score", nullable = false) // Renamed from 'points' to 'score'
    private int score = 0;

    @Column(name = "turn_count", nullable = false) // Track the number of turns taken
    private int turnCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    // Constructors
    public Player() {
        // Default constructor for JPA
    }

    public Player(String name, GameSession gameSession) {
        this.name = name;
        this.score = 0;
        this.turnCount = 0;
        this.gameSession = gameSession;
    }

    // Getters and Setters
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    // Helper Methods
    public void addScore(int scoreToAdd) {
        this.score += scoreToAdd;
    }

    public void resetScore() {
        this.score = 0;
    }

    public void incrementTurnCount() {
        this.turnCount++;
    }

    // toString, equals, hashCode
    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", score=" + score +
                ", turnCount=" + turnCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id != null && id.equals(player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
