package bd.trabalho.steamapi.repositories;

import bd.trabalho.steamapi.entity.Review;
import bd.trabalho.steamapi.entity.ReviewId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, ReviewId> {
}
