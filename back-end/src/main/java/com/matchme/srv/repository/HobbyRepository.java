package com.matchme.srv.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.matchme.srv.model.user.profile.Hobby;

import java.util.Set;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    
    /**
     * Find all hobbies associated with a user
     * 
     * @param userId The ID of the user
     * @return Set of hobbies associated with the user
     */
    @Query(value = 
        "SELECT h.* FROM hobby h " +
        "JOIN user_hobby uh ON h.id = uh.hobby_id " +
        "WHERE uh.user_id = :userId",
        nativeQuery = true)
    Set<Hobby> findByUserId(@Param("userId") Long userId);
}
  
