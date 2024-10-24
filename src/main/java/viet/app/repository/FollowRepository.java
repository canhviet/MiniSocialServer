package viet.app.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import viet.app.model.Follow;
import viet.app.model.User;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowingIdAndUserId(User user1, User user2);

    @Modifying
    @Transactional
    @Query("delete Follow f where f.followingId = :followingUser and f.userId = :user")
    void unFollowUser(@Param("followingUser") User followingUser, @Param("user") User user);
}
