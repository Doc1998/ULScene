package ULScene.service;

import ULScene.dto.CommentDto;
import ULScene.dto.CommentResponse;
import ULScene.exceptions.PostNotFoundException;
import ULScene.exceptions.ULSceneException;
import ULScene.mapper.CommentMapper;
import ULScene.model.Comment;
import ULScene.model.NotificationEmail;
import ULScene.model.Post;
import ULScene.model.User;
import ULScene.respository.CommentRepository;
import ULScene.respository.PostRepository;
import ULScene.respository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CommentService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final MailService mailService;
    private final MailContentBuilder mailContentBuilder;
    public void save(CommentDto commentDto){
        Post post = postRepository.findById(commentDto.getPostId()).orElseThrow(()->new ULSceneException("Not found"));
        Comment comment = commentMapper.map(commentDto,post, authService.getCurrentUser());
        commentRepository.save(comment);

        String message = mailContentBuilder.build(post.getUser().getUsername() + "posted a comment on your post");
        sendCommentNotification(message,post.getUser());
    }
    public void sendCommentNotification(String message, User user){
        mailService.sendMail(new NotificationEmail(user.getUsername()+ "Commented on your post", user.getEmail(),message ));
    }
    public List<CommentResponse> getAllCommentsForPost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException("Not found"));
        return commentRepository.findByPost(post).stream().map(commentMapper::mapToDto).collect(Collectors.toList());
    }
    public List<CommentResponse> getAllCommentsForUser(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("Not found"));
        return commentRepository.findAllByUser(user).stream().map(commentMapper::mapToDto).collect(Collectors.toList());
    }
    public List<CommentResponse> getAllCommentsForCurrentUser(){
        User user = authService.getCurrentUser();
        return commentRepository.findAllByUser(user).stream().map(commentMapper::mapToDto).collect(Collectors.toList());
    }
    public CommentResponse getCommentById(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ULSceneException("Not found"));
        return commentMapper.mapToDto(comment);
    }
}
