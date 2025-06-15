package bd.trabalho.steamapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id
    private String steamId;

    @Column(nullable = false)
    private String nickname;

    private String realName;

    private String profileImageUrl;

    private Date signDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "player_owns_game")
    private Set<Game> games = new HashSet<>();

    @OneToMany(mappedBy = "player")
    private Set<Review> reviews = new HashSet<>();

    @OneToMany
    private Set<PlayerPlaysGame> gamesPlayed = new HashSet<>();


}
