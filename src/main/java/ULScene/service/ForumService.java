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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ForumService {
    private final ForumRepository forumRepository;
    private final ForumMapper forumMapper;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRolesRepository userRolesRepository;

    @Transactional
    public ForumDto save(ForumDto forumDto){
        if(!forumRepository.existsByName(forumDto.getName())) {
            Forum save = forumRepository.save(forumMapper.mapDtoToForum(forumDto, authService.getCurrentUser()));
            if(forumDto.isPrivate()){
                save.setPrivate(true);
            }
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
    public boolean isForumModerator(String name){
        Forum forum = forumRepository.findByName(name).orElseThrow(() -> new ULSceneException("Forum not found"));
        List<User> users = forum.getModerators();
        if(users.contains(authService.getCurrentUser())){
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
        User userAdding = userRepository.findByUsername(moderatorRequest.getUsernameAdding()).orElseThrow(()-> new ULSceneException("User not found"));
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
    public Long deleteForum(Long id){
        Forum forum = forumRepository.findById(id).orElseThrow(()-> new ULSceneException("Not found"));
        List<Post> forumPosts = postRepository.findAllByForum(forum);
        System.out.println(forumPosts.size());
        if(forumPosts.size() > 0){
            for(int i =0; i < forumPosts.size();){
                System.out.println("here1");
                if(commentRepository.findByPost(forumPosts.get(i)).size() > 0){
                    for(int k = 0;k < commentRepository.findByPost(forumPosts.get(i)).size();) {
                        commentRepository.deleteByPost(forumPosts.get(i));
                        System.out.println("here2");
                        k++;
                    }
                    i++;
                }
                postRepository.deleteByForum(forum);
                System.out.println("here 3 ");
            }
        }
        forumRepository.deleteById(id);
        return id;
    }
}
