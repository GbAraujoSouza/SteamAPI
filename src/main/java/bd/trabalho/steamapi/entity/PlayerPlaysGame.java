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

    @EmbeddedId
    private PlayerPlaysGameId playerPlaysGameId;

    @ManyToOne
    @MapsId("steamId")
    @JoinColumn(name = "steam_id")
    private Player player;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id")
    private Game game;

    private Integer playtimeForever;

    public PlayerPlaysGame(Player player, Game game, Integer playtimeForever) {
        this.player = player;
        this.game = game;
        this.playtimeForever = playtimeForever;
        this.playerPlaysGameId = new PlayerPlaysGameId(player.getSteamId(), game.getAppId());
    }
}
