package by.homesite.gator.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import by.homesite.gator.domain.UserSearches;
import by.homesite.gator.service.dto.UserSearchesDTO;

/**
 * Spring Data  repository for the UserSearches entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserSearchesRepository extends JpaRepository<UserSearches, Long> {

    @Query("select userSearches from UserSearches userSearches where userSearches.user.login = ?#{principal.username}")
    List<UserSearches> findByUserIsCurrentUser();

    @Query("select userSearches from UserSearches userSearches where userSearches.user.login = ?#{principal.username} AND userSearches.name = ?1")
    List<UserSearches> findByName(String name);
}
