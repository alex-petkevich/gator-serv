package by.homesite.gator.repository;

import by.homesite.gator.domain.Site;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Site entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
    @Query("select site from Site site where site.user.login = ?#{principal.username}")
    List<Site> findByUserIsCurrentUser();
}
