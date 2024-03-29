package ULScene.respository;

import ULScene.model.Comment;
import ULScene.model.Post;
import ULScene.model.User;
import ULScene.model.Vote;
import javafx.geometry.Pos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
    Optional<Vote> findTopByCommentAndUserOrderByVoteIdDesc(Comment comment, User currentUser);
    void deleteAllByComment(Comment comment);
    void deleteAllByPost(Post post);
}
