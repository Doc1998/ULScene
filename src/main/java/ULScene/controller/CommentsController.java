package ULScene.controller;

import ULScene.dto.CommentDto;
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
        commentService.save(commentsDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/post/{id}")
    public ResponseEntity<List<CommentDto>> getAllCommentsForPost(@PathVariable long postId){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForPost(postId));
    }
    @GetMapping("/user/{username}")
    public ResponseEntity<List<CommentDto>> getAllCommentsForUser(@PathVariable String username){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForUser(username));
    }
}
