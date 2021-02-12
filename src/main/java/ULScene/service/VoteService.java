package ULScene.service;

import ULScene.dto.VoteCommentDto;
import ULScene.dto.VoteDto;
import ULScene.exceptions.PostNotFoundException;
import ULScene.exceptions.ULSceneException;
import ULScene.model.Comment;
import ULScene.model.Post;
import ULScene.model.User;
import ULScene.model.Vote;
import ULScene.respository.CommentRepository;
import ULScene.respository.PostRepository;
import ULScene.respository.UserRepository;
import ULScene.respository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static ULScene.model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post Not Found with ID - " + voteDto.getPostId()));
        User author = post.getUser();
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
        if (voteByPostAndUser.isPresent() &&
                voteByPostAndUser.get().getVoteType()
                        .equals(voteDto.getVoteType())) {
            throw new ULSceneException("You have already "
                    + voteDto.getVoteType() + "'d for this post");
        }
        if(voteByPostAndUser.isPresent() &&
                !voteByPostAndUser.get().getVoteType()
                        .equals(voteDto.getVoteType())) {
            if (UPVOTE.equals(voteDto.getVoteType())) {
                post.setVoteCount(post.getVoteCount() + 1);
                author.setLogos(author.getLogos() + 1);
            } else {
                post.setVoteCount(post.getVoteCount() - 1);
                author.setLogos(author.getLogos() - 1);
            }
        }
        if (UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
            author.setLogos(author.getLogos() + 1);

        } else {
            post.setVoteCount(post.getVoteCount() - 1);
            author.setLogos(author.getLogos() - 1);
        }
        voteRepository.save(mapToVote(voteDto, post));
        userRepository.save(author);
        postRepository.save(post);
    }

    private Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
    private Vote mapCommentToVote(VoteCommentDto voteDto, Comment comment) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .comment(comment)
                .user(authService.getCurrentUser())
                .build();
    }
    @Transactional
    public void createCommentVote(VoteCommentDto voteCommentDto){
        Comment comment = commentRepository.findById(voteCommentDto.getCommentId())
                .orElseThrow(() -> new PostNotFoundException("Comment Not Found with ID - " + voteCommentDto.getCommentId()));
        User author = comment.getUser();
        Optional<Vote> voteByCommentAndUser = voteRepository.findTopByCommentAndUserOrderByVoteIdDesc(comment, authService.getCurrentUser());
        if (voteByCommentAndUser.isPresent() &&
                voteByCommentAndUser.get().getVoteType()
                        .equals(voteCommentDto.getVoteType())) {
            throw new ULSceneException("You have already "
                    + voteCommentDto.getVoteType() + "'d for this comment");
        }
        if(voteByCommentAndUser.isPresent() &&
                !voteByCommentAndUser.get().getVoteType()
                        .equals(voteCommentDto.getVoteType())) {
            if (UPVOTE.equals(voteCommentDto.getVoteType())) {
                comment.setVoteCount(comment.getVoteCount() + 1);
                author.setLogos(author.getLogos() + 1);
            } else {
                comment.setVoteCount(comment.getVoteCount() - 1);
                author.setLogos(author.getLogos() - 1);
            }
        }
        if (UPVOTE.equals(voteCommentDto.getVoteType())) {
            comment.setVoteCount(comment.getVoteCount() + 1);
            author.setLogos(author.getLogos() + 1);

        } else {
            comment.setVoteCount(comment.getVoteCount() - 1);
            author.setLogos(author.getLogos() - 1);
        }
        voteRepository.save(mapCommentToVote(voteCommentDto, comment));
        userRepository.save(author);
        commentRepository.save(comment);
    }
}