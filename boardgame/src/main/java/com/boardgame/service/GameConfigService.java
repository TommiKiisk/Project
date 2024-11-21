package com.boardgame.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boardgame.model.GameConfig;
import com.boardgame.repository.GameConfigRepository;

@Service
public class GameConfigService {
    @Autowired
    private GameConfigRepository gameConfigRepository;

    public GameConfig saveGameConfig(GameConfig config) {
        return gameConfigRepository.save(config);
    }

    public Optional<GameConfig> getGameConfig(Long id) {
        return gameConfigRepository.findById(id);
    }
}