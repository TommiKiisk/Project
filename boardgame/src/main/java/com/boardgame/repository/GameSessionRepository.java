package com.boardgame.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


import com.boardgame.model.GameSession;

public interface GameSessionRepository extends CrudRepository<GameSession, Long> {
    // Find sessions by game name
    List<GameSession> findByGameName(String gameName);

    // Get active sessions (where endTime is null)
    List<GameSession> findByEndTimeIsNull();
}
