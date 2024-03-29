package ULScene.respository;

import ULScene.model.Comment;
import ULScene.model.Post;
import ULScene.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByPost (Post post);
    List<Comment> findAllByUser (User user);
    void deleteAllByPost(Post post);
}
