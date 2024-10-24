package viet.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import viet.app.model.Post;
import viet.app.model.User;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> getPostsByUser(User user);

    @Query("select p from User u join Follow fl on fl.userId.id = u.id join Post p on p.user.id = fl.followingId.id where u.id =:userId order by p.updatedAt desc")
    List<Post> getPostsAndSortByUser(@Param("userId") long userId);
}
