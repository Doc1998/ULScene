package ULScene.controller;

import ULScene.dto.ForumDto;
import ULScene.dto.JoinForumRequest;
import ULScene.service.ForumService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PostRemove;

@RestController
@RequestMapping("/api/forum")
@AllArgsConstructor
@Slf4j
public class ForumController {
    private final ForumService forumService;
    @PostMapping
    public ResponseEntity createForum(@RequestBody ForumDto forumDto) {
        return ResponseEntity.status(HttpStatus.OK).body(forumService.save(forumDto));
    }
    @GetMapping
    public ResponseEntity getAllForums(){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.getAll());
    }

    @GetMapping("/mostPopular")
    public ResponseEntity getAllForumsByPopularity(){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.getAllByPopular());
    }

    @GetMapping("/users/{name}")
    public ResponseEntity getAllMembers(@PathVariable String name){return ResponseEntity.status(HttpStatus.OK).body(forumService.getForumMembers(name));}

    @GetMapping("/{id}")
    public ResponseEntity getForum(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.getForum(id));
    }
    @PostMapping("/delete/{id}")
    public ResponseEntity deleteForum(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.deleteForum(id));
    }
    @PostMapping("/join")
    public ResponseEntity joinForum(@RequestBody JoinForumRequest joinForumRequest){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.joinForum(joinForumRequest));
    }
}