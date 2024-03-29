package by.homesite.gator.repository;

import by.homesite.gator.domain.UserProperties;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the UserProperties entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserPropertiesRepository extends JpaRepository<UserProperties, Long> {
    @Query("select userProperties from UserProperties userProperties where userProperties.user.login = ?#{principal.username}")
    List<UserProperties> findByUserIsCurrentUser();
}
