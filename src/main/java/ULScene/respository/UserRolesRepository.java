package ULScene.respository;

import ULScene.model.User;
import ULScene.model.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles,Long> {
    Optional<UserRoles> findByUser(User user);
}
