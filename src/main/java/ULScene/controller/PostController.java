package ULScene.controller;

import ULScene.dto.PostRequest;
import ULScene.dto.PostResponse;
import ULScene.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity createPost(@RequestBody PostRequest postRequest) {
        postService.save(postRequest);
        return new ResponseEntity(HttpStatus.CREATED);
    }
    @PostMapping("/delete")
    public ResponseEntity deletePost(@RequestBody Long id) {
        postService.deletePost(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity <List<PostResponse>> getAllPosts(){
       return ResponseEntity.ok(postService.getAllPosts());
    }
    @GetMapping("/id/{postId}")
    public ResponseEntity<PostResponse> getPostbyId(@PathVariable Long postId){
        return  ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity <List<PostResponse>> getAllPostsByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(postService.getAllPostsByUserId(userId));
    }
    @GetMapping("/currentUser")
    public ResponseEntity <List<PostResponse>> getAllPostsByCurrentUser(){
        return ResponseEntity.ok(postService.getAllPostsByCurrentUser());
    }
    @GetMapping("/by-user/{username}")
    public ResponseEntity <List<PostResponse>> getAllPostsByUsername(@PathVariable String username){
        return ResponseEntity.ok(postService.getAllPostsByUsername(username));
    }
    @GetMapping("/by-Forum/{id}")
    public ResponseEntity <List<PostResponse>> getAllPostsByForumId(@PathVariable Long id){
        return ResponseEntity.ok(postService.getAllPostsByForumId(id));
    }
    @GetMapping("/by-Forum-name/{name}")
    public ResponseEntity <List<PostResponse>> getAllPostsByForumName(@PathVariable String name) {
        return ResponseEntity.ok(postService.getAllPostsByForumName(name));
    }
    @GetMapping("by-Forum-name/{name}/mostPopular")
    public ResponseEntity <List<PostResponse>> getAllPostsByPopularity(@PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByPopular(name));
    }
    @GetMapping("by-Forum-name/{name}/bestOfWeek")
    public ResponseEntity <List<PostResponse>> getBestPostsOfWeek(@PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByPopular(name));
    }
}