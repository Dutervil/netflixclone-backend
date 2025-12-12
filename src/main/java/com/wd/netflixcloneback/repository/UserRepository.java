package com.wd.netflixcloneback.repository;

import com.wd.netflixcloneback.entity.User;
import com.wd.netflixcloneback.entity.Video;
import com.wd.netflixcloneback.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    Optional<User> findByVerificationToken(String verificationToken);

    Optional<User> findByPasswordResetToken(String passwordResetToken);

    long countByRole(Role role);

    long countByRoleAndActive(Role role, boolean active);
     @Query("""
          SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%',:search,'%'))
                                   OR LOWER(u.email) LIKE LOWER(CONCAT('%',:search,'%'))
         """)
    Page<User> searchUsers(@Param("search") String trim, Pageable pageable);
    @Query("select vid.id from User u join u.videos vid where u.email = :email and vid.id in :videoIds")
    Set<Long> findWatchListVideoIds(@Param("email") String email, @Param("videoIds") List<Long> videoIds);

    @Query("""
    SELECT v  FROM User u JOIN u.videos v WHERE u.id = :userId AND v.published = true
      AND (
            LOWER(v.title) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(v.description) LIKE LOWER(CONCAT('%', :search, '%'))
      )
""")
    Page<Video> searchWatchListByUserId(@Param("userId") Long userId,
                                        @Param("search") String search,
                                        Pageable pageable);

    @Query("""
    SELECT v  FROM User u JOIN u.videos v WHERE u.id = :userId AND v.published = true
      
""")
    Page<Video> findWatchListByUserId(@Param("userId") Long userId, Pageable pageable);
}
