package by.homesite.gator.repository;

import by.homesite.gator.domain.UserSites;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the UserSites entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserSitesRepository extends JpaRepository<UserSites, Long> {
    @Query("select userSites from UserSites userSites where userSites.user.login = ?#{principal.username}")
    List<UserSites> findByUserIsCurrentUser();
}
