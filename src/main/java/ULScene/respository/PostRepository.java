package ULScene.respository;

import ULScene.model.Forum;
import ULScene.model.Post;
import ULScene.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByForum (Forum forum);
    List<Post> findAllByUser  (User user);
    void  deleteByForum (Forum forum);
}
