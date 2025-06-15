package bd.trabalho.steamapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Friendship {

    @EmbeddedId
    private FriendshipId id;

    @ManyToOne
    @MapsId("player1Id")
    @JoinColumn(name = "player1_id")
    private Player player1;

    @ManyToOne
    @MapsId("player2Id")
    @JoinColumn(name = "player2_id")
    private Player player2;

    private Date friendshipDate;

    /**
     * Constructor that accepts a Unix timestamp (in seconds).
     * @param player1 The first player in the friendship.
     * @param player2 The second player in the friendship.
     * @param unixTimestamp The time of friendship creation as a Unix timestamp (seconds).
     */
    public Friendship(Player player1, Player player2, long unixTimestamp) {
        this.player1 = player1;
        this.player2 = player2;
        this.id = new FriendshipId(player1.getSteamId(), player2.getSteamId());
        // Convert Unix timestamp (seconds) to Date (milliseconds)
        this.friendshipDate = new Date(unixTimestamp * 1000L);
    }
}