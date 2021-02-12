package ULScene.controller;

import ULScene.dto.VoteCommentDto;
import ULScene.dto.VoteDto;
import ULScene.service.PostService;
import ULScene.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@AllArgsConstructor
public class VoteController {
    private final VoteService voteService;
    private final PostService postService;
    @PostMapping
    public ResponseEntity createVote(@RequestBody VoteDto voteDto) {
        voteService.vote(voteDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/comment")
    public ResponseEntity createCommentVote(@RequestBody VoteCommentDto voteDto) {
        voteService.createCommentVote(voteDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/user/logos")
    public ResponseEntity getUserLogos(){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getUserLogos());
    }
    @GetMapping("/user/logos/{username}")
    public ResponseEntity<Integer> getUserLogos(@PathVariable String username){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getUsersLogos(username));
    }
    @GetMapping("/user/joinDate")
    public ResponseEntity getUserCreatedDate(){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getUserCreatedDate());
    }
    @GetMapping("/user/joinDate/{username}")
    public ResponseEntity getUsersCreatedDate(@PathVariable String username){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getUsersCreatedDate(username));
    }
}
