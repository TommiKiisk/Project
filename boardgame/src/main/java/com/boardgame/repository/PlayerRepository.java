package com.boardgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.boardgame.model.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
}
