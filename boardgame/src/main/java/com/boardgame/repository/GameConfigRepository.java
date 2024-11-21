package com.boardgame.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.boardgame.model.GameConfig;


public interface GameConfigRepository extends CrudRepository<GameConfig, Long> {
    // Add custom queries if needed, e.g., find by name
    Optional<GameConfig> findByName(String name);
}
