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

    private String country;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "player_owns_game",
            joinColumns = @JoinColumn(name = "steam_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private Set<Game> games = new HashSet<>();

    @OneToMany(mappedBy = "player")
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "player")
    private Set<PlayerPlaysGame> gamesPlayed = new HashSet<>();

    public Player(String steamId, String nickname, String realName, String profileImageUrl, Date signDate,  String country) {
        this.steamId = steamId;
        this.nickname = nickname;
        this.realName = realName;
        this.profileImageUrl = profileImageUrl;
        this.signDate = signDate;
        this.country = country;
    }

}
