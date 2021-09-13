package by.homesite.gator.repository;

import by.homesite.gator.domain.UserProperties;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the UserProperties entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserPropertiesRepository extends JpaRepository<UserProperties, Long> {

    @Query("select userProperties from UserProperties userProperties where userProperties.user.login = ?#{principal.username}")
    List<UserProperties> findByUserIsCurrentUser();

}
