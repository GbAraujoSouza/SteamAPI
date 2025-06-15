package bd.trabalho.steamapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    private Integer markerId;

    private String name;

    @ManyToMany(mappedBy = "markers")
    private Set<Game> games = new HashSet<>();

    public Tag(Integer markerId, String name) {
        this.markerId = markerId;
        this.name = name;
    }
}