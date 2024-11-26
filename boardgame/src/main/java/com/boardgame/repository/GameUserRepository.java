package com.boardgame.repository;


import org.springframework.data.repository.CrudRepository;

import com.boardgame.model.GameUser;

public interface GameUserRepository extends CrudRepository<GameUser, Long> {
    GameUser findByUsername(String username);

    boolean existsByUsername(String string);
}
