package ULScene.mapper;

import ULScene.dto.CommentDto;
import ULScene.dto.CommentResponse;
import ULScene.model.*;
import ULScene.respository.PostRepository;
import ULScene.respository.VoteRepository;
import ULScene.service.AuthService;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static ULScene.model.VoteType.DOWNVOTE;
import static ULScene.model.VoteType.UPVOTE;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private PostRepository postRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "commentsDto.text")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "post", source = "post")
    public abstract Comment map(CommentDto commentsDto, Post post, User user);

    @Mapping(target = "userName", expression = "java(comment.getUser().getUsername())")
    @Mapping(target = "postName",expression = "java(getPostName(comment))")
    @Mapping(target = "duration", expression = "java(getDuration(comment))")
    @Mapping(target = "upVote",expression = "java(isCommentUpVoted(comment))")
    @Mapping(target = "downVote" ,expression = "java(isCommentDownVoted(comment))")
    public abstract CommentResponse mapToDto(Comment comment);

    String getDuration(Comment comment) {
        return TimeAgo.using(comment.getCreatedDate().toEpochMilli());
    }

    boolean isCommentUpVoted(Comment comment){
        return checkVoteType(comment,UPVOTE);
    }
    boolean isCommentDownVoted(Comment comment){
        return checkVoteType(comment,DOWNVOTE);
    }
    private boolean checkVoteType(Comment comment, VoteType voteType){
        if(authService.isLoggedIn()){
            Optional<Vote> voteForCommentByUser =
                    voteRepository.findTopByCommentAndUserOrderByVoteIdDesc(comment, authService.getCurrentUser());
            return voteForCommentByUser.filter(vote -> vote.getVoteType().equals(voteType)).isPresent();
        }
        return false;
    }
     String getPostName(Comment comment){
        Post post = comment.getPost();
        return post.getPostName();
    }

}
