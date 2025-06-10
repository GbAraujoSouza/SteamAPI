package bd.trabalho.steamapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PlayerPlaysGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    private Integer playtimeForever;

    public PlayerPlaysGame(Long id, Player player, Game game, Integer playtimeForever) {
        this.id = id;
        this.player = player;
        this.game = game;
        this.playtimeForever = playtimeForever;
    }
}
