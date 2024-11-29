package com.boardgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.boardgame.model.GameUser;

public interface GameUserRepository extends JpaRepository<GameUser, Long> {

    GameUser findByUsername(String username);

    boolean existsByUsername(String username);
}
