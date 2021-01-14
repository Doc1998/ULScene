package ULScene.service;


import ULScene.dto.ForumDto;
import ULScene.dto.JoinForumRequest;
import ULScene.exceptions.ULSceneException;
import ULScene.mapper.ForumMapper;
import ULScene.model.Forum;
import ULScene.model.Post;
import ULScene.model.User;
import ULScene.respository.CommentRepository;
import ULScene.respository.ForumRepository;
import ULScene.respository.PostRepository;
import ULScene.respository.UserRepository;
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

    @Transactional
    public ForumDto save(ForumDto forumDto){
        Forum save = forumRepository.save(forumMapper.mapDtoToForum(forumDto,authService.getCurrentUser()));
        forumDto.setId(save.getId());
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
    public List<User> getForumMembers(String name){
        Forum forum = forumRepository.findByName(name).orElseThrow(() -> new ULSceneException("Forum not found"));
        List<User> users = forum.getUsers();
        return users.stream().collect(Collectors.toList());
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

    public ForumDto getForum(Long id) {
        Forum forum = forumRepository.findById(id).orElseThrow(() -> new ULSceneException("Forum not found"));
        return forumMapper.mapForumToDto(forum);
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
