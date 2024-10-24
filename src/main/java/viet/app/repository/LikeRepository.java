package viet.app.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import viet.app.model.Like;
import viet.app.model.Post;
import viet.app.model.User;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndPost(User user, Post post);

    @Modifying
    @Transactional
    @Query("delete Like l where l.post = :post and l.user = :user")
    void unLikePost(@Param("user") User user, @Param("post") Post post);
}
