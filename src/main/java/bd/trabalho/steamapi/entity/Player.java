package bd.trabalho.steamapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Player {
    @Id
    private String steamId;
    private String realName;

    @OneToMany(mappedBy = "player")
    private Set<PlayerPlaysGame> playedGames = new HashSet<>();

    public Player(String steamId, String realName) {
        this.steamId = steamId;
        this.realName = realName;
    }
}
