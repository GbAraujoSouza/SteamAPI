package bd.trabalho.steamapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Game {

    @Id
    private String appId;

    private String name;
    private LocalDate releaseDate;
    private String description;
    private Double price;


    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Player> players;

    // Relationships
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "game_tags",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> markers = new HashSet<>();

    @OneToMany(mappedBy = "game")
    private Set<Review> reviews = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "game_developers")
    private Set<Developer> developers = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "game_publishers")
    private Set<Publisher> publishers = new HashSet<>();

//    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<GameOwnership> owners = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlayerPlaysGame> playSessions = new HashSet<>();

    public Game(String appId, String name) {
        this.appId = appId;
        this.name = name;
    }

//    // Helper methods
//    public void addDeveloper(String developer) {
//        this.developers.add(developer);
//    }
//
//    public void addPublisher(String publisher) {
//        this.publishers.add(publisher);
//    }
//
//    public void removeDeveloper(String developer) {
//        this.developers.remove(developer);
//    }
//
//    public void removePublisher(String publisher) {
//        this.publishers.remove(publisher);
//    }
}