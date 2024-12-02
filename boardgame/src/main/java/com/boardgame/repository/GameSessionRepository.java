package com.boardgame.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;


import com.boardgame.model.GameSession;

public interface GameSessionRepository extends CrudRepository<GameSession, Long> {

    @SuppressWarnings("null")
    List<GameSession> findAll();
    Optional<GameSession> findFirstByActiveTrueOrderByStartTimeDesc();
}
