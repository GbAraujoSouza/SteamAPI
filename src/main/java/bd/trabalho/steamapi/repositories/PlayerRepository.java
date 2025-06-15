package bd.trabalho.steamapi.repositories;

import bd.trabalho.steamapi.entity.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, String> {
}
