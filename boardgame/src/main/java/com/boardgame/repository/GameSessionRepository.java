package com.boardgame.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;


import com.boardgame.model.GameSession;

public interface GameSessionRepository extends CrudRepository<GameSession, Long> {


    Optional<GameSession> findFirstByActiveTrueOrderByStartTimeDesc();
}
