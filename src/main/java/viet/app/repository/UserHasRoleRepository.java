package viet.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import viet.app.model.Role;
import viet.app.model.User;
import viet.app.model.UserHasRole;

import java.util.Set;

@Repository
public interface UserHasRoleRepository extends JpaRepository<UserHasRole, Integer> {
    boolean existsByUserAndRole(User user, Role role);
}
