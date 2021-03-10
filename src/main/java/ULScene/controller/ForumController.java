package ULScene.controller;

import ULScene.dto.ForumDto;
import ULScene.dto.JoinForumRequest;
import ULScene.dto.ModeratorRequest;
import ULScene.model.Forum;
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
    @GetMapping("/mostActive")
    public ResponseEntity getAllForumsByActivity(){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.getAllByActive());
    }

    @GetMapping("/{name}/users")
    public ResponseEntity getAllMembers(@PathVariable String name){return ResponseEntity.status(HttpStatus.OK).body(forumService.getForumMembers(name));}

    @GetMapping("/{name}/mods")
    public ResponseEntity getAllModerators(@PathVariable String name){return ResponseEntity.status(HttpStatus.OK).body(forumService.getForumModerators(name));}

    @GetMapping("/{id}")
    public ResponseEntity getForum(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.getForum(id));
    }
    @GetMapping("/checkMembership/{name}")
    public ResponseEntity checkMembership(@PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.isForumMember(name));
    }
    @GetMapping("/checkBanned/{name}")
    public ResponseEntity checkBanned(@PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.isBanned(name));
    }
    @GetMapping("/checkModerator/{name}")
    public ResponseEntity checkModerator(@PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.isForumModerator(name));
    }
    @GetMapping("/checkAdmin")
    public ResponseEntity checkAdmin(){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.checkAdmin());
    }
    @GetMapping("/by-name/{name}")
    public ResponseEntity getForum(@PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.getForumByName(name));
    }
    @PostMapping("/delete")
    public ResponseEntity deleteForum(@RequestBody Long id){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.deleteForum(id));
    }
    @PostMapping("/join")
    public ResponseEntity joinForum(@RequestBody JoinForumRequest joinForumRequest){
        forumService.joinForum(joinForumRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/leave")
    public ResponseEntity leaveForum(@RequestBody JoinForumRequest joinForumRequest){
        forumService.leaveForum(joinForumRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/{name}/addMod")
    public ResponseEntity addModToForum(@RequestBody ModeratorRequest moderatorRequest){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.AddModerator(moderatorRequest));
    }
    @PostMapping("/{name}/ban")
    public ResponseEntity banUserFromForum(@RequestBody ModeratorRequest moderatorRequest){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.banUser(moderatorRequest));
    }
    @PostMapping("/{name}/removeMod")
    public ResponseEntity RemoveModToForum(@RequestBody ModeratorRequest moderatorRequest){
        return ResponseEntity.status(HttpStatus.OK).body(forumService.RemoveModerator(moderatorRequest));
    }
}