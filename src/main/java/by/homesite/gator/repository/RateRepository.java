package by.homesite.gator.repository;

import by.homesite.gator.domain.Rate;
import by.homesite.gator.domain.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Rate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    Optional<Rate> findOneByCode(String code);

}
