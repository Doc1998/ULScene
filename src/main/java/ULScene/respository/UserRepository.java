package ULScene.respository;

import ULScene.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail (String email);
    Optional<User> findByEmailAndUsername(String email,String username);
    boolean existsByEmailAndEnabled (String email, boolean isEnabled);
    boolean existsByUsernameAndEnabled (String username,boolean isEnabled);
    boolean  existsByUsername (String username);
    boolean existsByEmail   (String email);
}
