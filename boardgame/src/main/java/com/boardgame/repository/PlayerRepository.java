package com.boardgame.repository;

import org.springframework.data.repository.CrudRepository;

import com.boardgame.model.Player;

import java.util.List;

public interface PlayerRepository extends CrudRepository<Player, Long> {
    // Custom query to find a player by name within a specific session if needed
    List<Player> findByName(String name);
}
