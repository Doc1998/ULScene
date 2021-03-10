package ULScene.respository;

import ULScene.model.Forum;
import ULScene.model.Post;
import ULScene.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByForum (Forum forum);
    List<Post> findAllByForumAndCreatedDateIsLessThanEqual(Forum forum,Instant weekAgo);
    List<Post> findAllByUser  (User user);
    Optional<Post> deleteById (long id);
    void  deleteByForum (Forum forum);
}
