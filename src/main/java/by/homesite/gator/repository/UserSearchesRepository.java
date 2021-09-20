package by.homesite.gator.repository;

import by.homesite.gator.domain.UserSearches;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the UserSearches entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserSearchesRepository extends JpaRepository<UserSearches, Long> {
    @Query("select userSearches from UserSearches userSearches where userSearches.user.login = ?#{principal.username}")
    List<UserSearches> findByUserIsCurrentUser();

    @Query(
        "select userSearches from UserSearches userSearches where userSearches.user.login = ?#{principal.username} AND userSearches.name = ?1"
    )
    List<UserSearches> findByName(String name);
}
