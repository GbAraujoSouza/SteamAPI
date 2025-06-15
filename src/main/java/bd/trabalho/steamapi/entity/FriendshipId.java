package bd.trabalho.steamapi.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class FriendshipId {
    private String player1Id;
    private String player2Id;

    public FriendshipId(String player1Id, String player2Id) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
    }
}
