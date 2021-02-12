package ULScene.controller;

import ULScene.dto.CommentDto;
import ULScene.dto.CommentResponse;
import ULScene.dto.PostRequest;
import ULScene.model.Post;
import ULScene.respository.CommentRepository;
import ULScene.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments/")
@AllArgsConstructor
public class CommentsController {
    private final CommentService commentService;
    @PostMapping
    public ResponseEntity createComment(@RequestBody CommentDto commentsDto) {
        if(commentsDto.getText().length() > 0) {
            commentService.save(commentsDto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/post/{id}")
    public ResponseEntity<List<CommentResponse>> getAllCommentsForPost(@PathVariable long id){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForPost(id));
    }
    @GetMapping("/user/{username}")
    public ResponseEntity<List<CommentResponse>> getAllCommentsForUser(@PathVariable String username){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForUser(username));
    }
    @GetMapping("/user/currentUser")
    public ResponseEntity<List<CommentResponse>> getAllCommentsForUser(){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForCurrentUser());
    }
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentById(id));
    }
}
