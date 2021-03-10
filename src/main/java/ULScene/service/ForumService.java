package ULScene.service;


import ULScene.dto.ForumDto;
import ULScene.dto.JoinForumRequest;
import ULScene.dto.ModeratorRequest;
import ULScene.exceptions.ForumNotFoundException;
import ULScene.exceptions.ULSceneException;
import ULScene.mapper.ForumMapper;
import ULScene.model.Forum;
import ULScene.model.Post;
import ULScene.model.User;
import ULScene.model.UserRoles;
import ULScene.respository.*;
import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.geometry.Pos;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ForumService {
    private final ForumRepository forumRepository;
    private final ForumMapper forumMapper;
    private final AuthService authService;
    private final PostService postService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRolesRepository userRolesRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public ForumDto save(ForumDto forumDto){
        if(!forumRepository.existsByName(forumDto.getName())) {
            Forum save = forumRepository.save(forumMapper.mapDtoToForum(forumDto, authService.getCurrentUser()));
            if(forumDto.isPrivate()){
                save.setPrivate(true);
            }
            save.setBackground(forumDto.getBackground());
            forumDto.setId(save.getId());
        }
        return forumDto;
    }

    public List<ForumDto> getAll(){
        return forumRepository.findAll().stream().map(forumMapper::mapForumToDto).collect(Collectors.toList());
    }
    public List<ForumDto> getAllByPopular(){
        List<ForumDto> dtos = forumRepository.findAll().stream().map(forumMapper::mapForumToDto).collect(Collectors.toList());
        Collections.sort(dtos, new Comparator<ForumDto>() {
            @Override
            public int compare(ForumDto o1, ForumDto o2) {
                if(o1.getNumberOfUsers() > o2.getNumberOfUsers()){
                    return -1;
                }else {
                    return 1;
                }
            }
        });
        return dtos;
    }
    public List<ForumDto> getAllByActive(){
        List<ForumDto> dtos = forumRepository.findAll().stream().map(forumMapper::mapForumToDto).collect(Collectors.toList());
        Collections.sort(dtos, new Comparator<ForumDto>() {
            @Override
            public int compare(ForumDto o1, ForumDto o2) {
                if(o1.getNumberOfPosts() > o2.getNumberOfPosts()){
                    return -1;
                }else {
                    return 1;
                }
            }
        });
        return dtos;
    }

    public List<User> getForumMembers(String name){
        Forum forum = forumRepository.findByName(name).orElseThrow(() -> new ULSceneException("Forum not found"));
        List<User> users = forum.getUsers();
        return users.stream().collect(Collectors.toList());
    }
    public List<User> getForumModerators(String name){
        Forum forum = forumRepository.findByName(name).orElseThrow(() -> new ULSceneException("Forum not found"));
        List<User> users = forum.getModerators();
        return users.stream().collect(Collectors.toList());
    }
    public boolean isForumMember(String name){
        Forum forum = forumRepository.findByName(name).orElseThrow(() -> new ULSceneException("Forum not found"));
        List<User> users = forum.getUsers();
        if(users.contains(authService.getCurrentUser())){
            return true;
        }
        return false;
    }
    public boolean isBanned(String name){
        Forum forum = forumRepository.findByName(name).orElseThrow(() -> new ULSceneException("Forum not found"));
        List<User> users = forum.getBannedUsers();
        if(users.contains(authService.getCurrentUser())){
            return true;
        }
        return false;
    }
    public boolean isForumModerator(String name){
        Forum forum = forumRepository.findByName(name).orElseThrow(() -> new ULSceneException("Forum not found"));
        List<User> users = forum.getModerators();
        if(users.contains(authService.getCurrentUser())){
            return true;
        }
        return false;
    }
    public boolean checkAdmin(){
        User user = authService.getCurrentUser();
        UserRoles userRole = userRolesRepository.findByUser(user).orElseThrow(()->new ULSceneException("No role found"));
        if(userRole.getRole().getId() == 2){
            return true;
        }
        return false;
    }
    public String joinForum(JoinForumRequest joinForumRequest){
        Forum forum = forumRepository.findByName(joinForumRequest.getForumName()).orElseThrow(()-> new ULSceneException("Forum not found"));
        User user = userRepository.findByUsername(joinForumRequest.getUsername()).orElseThrow(()-> new ULSceneException("User not found"));
        List<User> userList = forum.getUsers();
        if(!userList.contains(user)) {
            userList.add(user);
        }
        forum.setUsers(userList);
        forumRepository.save(forum);
        return joinForumRequest.getForumName();
    }
    public int banUser(ModeratorRequest moderatorRequest) {
        User userBeingAdded = userRepository.findByUsername(moderatorRequest.getUsernameBeingAdded()).orElseThrow(() -> new ULSceneException("User not found"));
        User userAdding = authService.getCurrentUser();
        Forum forum = forumRepository.findByName(moderatorRequest.getForumName()).orElseThrow(() -> new ForumNotFoundException("Forum not found"));
        List<User> banned = forum.getBannedUsers();
        List<User> users = forum.getUsers();
        UserRoles userRole = userRolesRepository.findByUser(userAdding).orElseThrow(() -> new ULSceneException("Not found"));
        UserRoles bannedRole = userRolesRepository.findByUser(userBeingAdded).orElseThrow(() -> new ULSceneException("Not found"));
        boolean exists = false;
        List<User> currentMods = forum.getModerators();
        if (currentMods.contains(userAdding) || forum.getUser() == userAdding || userRole.getRole().getId() == 2) {
            for (int i = 0; i < banned.size(); i++) {
                if (banned.get(i) == userBeingAdded) {
                    exists = true;
                }
            }
            if(!exists) {
                if(users.contains(userBeingAdded)){
                    users.remove(userBeingAdded);
                    forum.setUsers(users);
                }
                if(userBeingAdded != forum.getUser() && bannedRole.getRole().getId() != 2){
                    banned.add(userBeingAdded);
                    forum.setBannedUsers(banned);
                    forumRepository.save(forum);
                    return 1;
                }
            }
        }
        return 0;
    }

    public String leaveForum(JoinForumRequest joinForumRequest){
        Forum forum = forumRepository.findByName(joinForumRequest.getForumName()).orElseThrow(()-> new ULSceneException("Forum not found"));
        User user = userRepository.findByUsername(joinForumRequest.getUsername()).orElseThrow(()-> new ULSceneException("User not found"));
        List<User> userList = forum.getUsers();
        if(userList.contains(user) && forum.getUser() != user) {
            userList.remove(user);
        }
        forum.setUsers(userList);
        forumRepository.save(forum);
        return joinForumRequest.getForumName();
    }

    public ForumDto getForum(Long id) {
        Forum forum = forumRepository.findById(id).orElseThrow(() -> new ULSceneException("Forum not found"));
        return forumMapper.mapForumToDto(forum);
    }
    public ForumDto getForumByName(String name) {
        Forum forum = forumRepository.findByName(name).orElseThrow(() -> new ULSceneException("Forum not found"));
        return forumMapper.mapForumToDto(forum);
    }
    @Transactional
    public int AddModerator(ModeratorRequest moderatorRequest){
        User userBeingAdded = userRepository.findByUsername(moderatorRequest.getUsernameBeingAdded()).orElseThrow(()-> new ULSceneException("User not found"));
        User userAdding = authService.getCurrentUser();
        Forum forum = forumRepository.findByName(moderatorRequest.getForumName()).orElseThrow(()-> new ForumNotFoundException("Forum not found"));
        List<User> members = forum.getUsers();
        UserRoles userRole = userRolesRepository.findByUser(userAdding).orElseThrow(()-> new ULSceneException("Not found"));
        boolean exists = false;
        List<User> currentMods = forum.getModerators();
        if(currentMods.contains(userAdding) || forum.getUser() == userAdding || userRole.getRole().getId() == 2){
           for(int i = 0; i < currentMods.size();i++) {
               if (currentMods.get(i) == userBeingAdded) {
                    exists = true;
               }
           }
           if(!exists) {
               if(!members.contains(userBeingAdded)){
                   members.add(userBeingAdded);
                   forum.setUsers(members);
               }
               currentMods.add(userBeingAdded);
               forum.setModerators(currentMods);
               forumRepository.save(forum);
               return 1;
           }
        }
        System.out.println(forum.getUser().getUsername());
        System.out.println(userAdding.getUsername());
        return 0;
    }
    @Transactional
    public int RemoveModerator(ModeratorRequest moderatorRequest){
        User userBeingRemoved = userRepository.findByUsername(moderatorRequest.getUsernameBeingAdded()).orElseThrow(()-> new ULSceneException("User not found"));
        User userRemoving = authService.getCurrentUser();
        Forum forum = forumRepository.findByName(moderatorRequest.getForumName()).orElseThrow(()-> new ForumNotFoundException("Forum not found"));
        List<User> members = forum.getUsers();
        UserRoles userRole = userRolesRepository.findByUser(userRemoving).orElseThrow(()-> new ULSceneException("Not found"));
        boolean exists = false;
        List<User> currentMods = forum.getModerators();
        if(currentMods.contains(userRemoving) || forum.getUser() == userRemoving || userRole.getRole().getId() == 2){

                if (currentMods.contains(userBeingRemoved)) {
                    currentMods.remove(userBeingRemoved);
                }

                forum.setModerators(currentMods);
                forumRepository.save(forum);
                return 1;
            }
             return 0;
        }

    @Transactional
    public Long deleteForum(Long id){
        Forum forum = forumRepository.findById(id).orElseThrow(()-> new ULSceneException("Not found"));
        List<Post> forumPosts = postRepository.findAllByForum(forum);
        System.out.println(forumPosts.size());
        if(forumPosts.size() > 0){
            for(int i =0; i < forumPosts.size();){
                System.out.println("here1");
                    voteRepository.deleteAllByPost(forumPosts.get(i));
                    postService.deletePost(forumPosts.get(i).getPostId());
                    i++;
                System.out.println("Done");
            }
        }
        if(forum.getModerators().size() > 0 ){
            List<User> emptyList = new ArrayList<User>();
            forum.setModerators(emptyList);
            forum.setUsers(emptyList);
        }
        forumRepository.deleteById(id);
        return id;
    }
}
