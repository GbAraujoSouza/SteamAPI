package bd.trabalho.steamapi.repositories;

import bd.trabalho.steamapi.entity.Developer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperRepository extends JpaRepository<Developer, String> {
}
