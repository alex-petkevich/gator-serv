package by.homesite.gator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import by.homesite.gator.domain.UserSearches;

/**
 * Spring Data  repository for the UserSearches entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserSearchesRepository extends JpaRepository<UserSearches, Long> {

    @Query("select userSearches from UserSearches userSearches where userSearches.user.login = ?#{principal.username}")
    List<UserSearches> findByUserIsCurrentUser();

}
