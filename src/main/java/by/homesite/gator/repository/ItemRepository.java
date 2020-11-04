package by.homesite.gator.repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import by.homesite.gator.domain.Item;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Item entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Modifying
    @Query("delete from Item i where i.updatedAt > :date")
	List<Integer> deleteOldItems(@Param("date") LocalDate date);
}
