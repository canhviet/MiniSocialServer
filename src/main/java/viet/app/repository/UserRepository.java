package viet.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import viet.app.model.Follow;
import viet.app.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query(value = "select r.name from UserHasRole ur inner join Role r on r.id = ur.role.id inner join User u on u.id = ur.user.id where ur.user.id =:userId")
    List<String> findAllRolesByUserId(Long userId);

    @Query("SELECT f.followingId FROM User u inner join Follow f on u.id = f.userId.id WHERE f.userId.id = ?1")
    List<User> findAllUsersExceptThisUserId(long userId);

    @Query("select u from User u where u.name like %:s% and u.id <> :userId")
    List<User> searchUser(@Param("s") String s, @Param("userId") long userId);
}
