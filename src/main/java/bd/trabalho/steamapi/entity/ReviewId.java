package bd.trabalho.steamapi.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ReviewId implements Serializable {
    private String steamId;
    private String gameId;

    public ReviewId(String steamId, String gameId) {
        this.steamId = steamId;
        this.gameId = gameId;
    }

}