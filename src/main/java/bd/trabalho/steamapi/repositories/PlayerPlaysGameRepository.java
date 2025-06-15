package bd.trabalho.steamapi.repositories;

import bd.trabalho.steamapi.entity.PlayerPlaysGame;
import bd.trabalho.steamapi.entity.PlayerPlaysGameId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerPlaysGameRepository extends JpaRepository<PlayerPlaysGame, PlayerPlaysGameId> {
}
