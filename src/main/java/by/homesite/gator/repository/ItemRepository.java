package by.homesite.gator.repository;

import java.time.ZonedDateTime;
import java.util.List;

import by.homesite.gator.domain.Item;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Spring Data  repository for the Item entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Transactional
    @Modifying
    @Query("delete from Item i where i.updatedAt < :date")
	void deleteOldItems(@Param("date") ZonedDateTime date);

    @Query("select i from Item i where i.updatedAt < :date")
    List<Item> findOldItems(@Param("date") ZonedDateTime date);
}
