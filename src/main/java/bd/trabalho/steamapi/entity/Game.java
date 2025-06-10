package bd.trabalho.steamapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a single game on Steam.
 * The primary key is the appId.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Game {

    @Id
    private Integer appId;

    private String name;

    public Game(Integer appId, String name) {
        this.appId = appId;
        this.name = name;
    }
}
