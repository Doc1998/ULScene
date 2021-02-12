package ULScene.respository;


import ULScene.model.Comment;
import ULScene.model.Forum;
import ULScene.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForumRepository extends JpaRepository<Forum,Long> {
    Optional<Forum> findByName (String forumName);
    Optional<Forum> findById (Long id);
    Optional<Forum> findByUser (User user);
    Optional<Forum> deleteById (long Id);
    Boolean existsByName(String name);

}