package bd.trabalho.steamapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Review implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String reviewText;
    private Integer score;
    private Date reviewDate;

    @EmbeddedId
    private ReviewId reviewId;

    @ManyToOne
    @MapsId("steamId")
    private Player player;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name="game_id")
    private Game game;

}
