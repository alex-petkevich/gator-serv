package by.homesite.gator.repository;

import by.homesite.gator.domain.Rate;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Rate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    Optional<Rate> findOneByCode(String code);
}
