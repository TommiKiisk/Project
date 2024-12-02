package com.boardgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.boardgame.model.Player;
import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    // Custom query to find players by name
    List<Player> findByName(String name);

    // Custom query to find players by score
    List<Player> findByScoreGreaterThanEqual(int score);

    // Custom query to find players by a part of their name
    List<Player> findByNameContaining(String partOfName);

    // Custom query using @Query annotation
    @Query("SELECT p FROM Player p WHERE p.score >= :score")
    List<Player> findPlayersWithScoreGreaterThan(@Param("score") int score);

    // Find all players sorted by score in descending order
    List<Player> findAllByOrderByScoreDesc();
}
