package bd.trabalho.steamapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single publisher on Steam.
 * The appId and the name are the primary keys.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Publisher {

    @Id
    private String name;

    @ManyToMany(mappedBy = "publishers")
    private Set<Game> games = new HashSet<>();

    public Publisher(String name) {
        this.name = name;
    }
}