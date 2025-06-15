package bd.trabalho.steamapi.repositories;

import bd.trabalho.steamapi.entity.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, String> {
}
