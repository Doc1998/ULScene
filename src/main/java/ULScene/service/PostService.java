package ULScene.service;

import ULScene.dto.ForumDto;
import ULScene.dto.PostRequest;
import ULScene.dto.PostResponse;
import ULScene.exceptions.ForumNotFoundException;
import ULScene.exceptions.ULSceneException;
import ULScene.mapper.PostMapper;
import ULScene.model.Comment;
import ULScene.model.Forum;
import ULScene.model.Post;
import ULScene.model.User;
import ULScene.respository.*;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import javafx.geometry.Pos;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
@Transactional
public class PostService {
    private final ForumRepository forumRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;


    public void save(PostRequest postRequest){
        Forum forum = forumRepository.findByName(postRequest.getForumName())
                .orElseThrow(() -> new ForumNotFoundException(postRequest.getForumName()));
        postRepository.save(postMapper.map(postRequest,forum,authService.getCurrentUser()));
        List<Post> postList = postRepository.findAllByForum(forum);
        forum.setPosts(postList);
        forumRepository.save(forum);

    }
    @Transactional (readOnly = true)
    public PostResponse getPost(long postId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new ULSceneException("Not found"));
        return postMapper.mapToDto(post);
    }
    public List<PostResponse> getPostsByPopular(String name){
        Forum forum = forumRepository.findByName(name).orElseThrow(()-> new ForumNotFoundException("Aint there"));
        List<PostResponse> dtos = postRepository.findAllByForum(forum).stream().map(postMapper::mapToDto).collect(Collectors.toList());
        Collections.sort(dtos, new Comparator<PostResponse>() {
            @Override
            public int compare(PostResponse o1, PostResponse o2) {
                if(o1.getVoteCount() > o2.getVoteCount()){
                    return -1;
                }else {
                    return 1;
                }
            }
        });
        return dtos;
    }
    public List<PostResponse> getBestPostsOfTheWeek(String name){
        Instant meTime = Instant.now();
        Instant weekAgo = meTime.minus(20, ChronoUnit.DAYS);
        Forum forum = forumRepository.findByName(name).orElseThrow(()-> new ForumNotFoundException("Aint there"));
        List<PostResponse> dtos = postRepository.findAllByForumAndCreatedDateIsLessThanEqual(forum,weekAgo).stream().map(postMapper::mapToDto).collect(Collectors.toList());
        Collections.sort(dtos, new Comparator<PostResponse>() {
            @Override
            public int compare(PostResponse o1, PostResponse o2) {
                if(o1.getVoteCount() > o2.getVoteCount()){
                    return -1;
                }else {
                    return 1;
                }
            }
        });
        return dtos;
    }
    @Transactional
    public void deletePost(long postId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new ULSceneException(("Not found")));
        List<Comment> commentList = commentRepository.findByPost(post);
        for(int i = 0;i < commentRepository.findByPost(post).size();i++){
            voteRepository.deleteAllByComment(commentList.get(i));
        }
        commentRepository.deleteAllByPost(post);
        Forum forum = post.getForum();
        System.out.println(post.getPostName());
        System.out.println(forum.getName());
        List<Post> forumPosts = forum.getPosts();
        forumPosts.remove(post);
        forum.setPosts(forumPosts);
        forumRepository.save(forum);
        voteRepository.deleteAllByPost(post);
        postRepository.deleteById(postId);
    }
    @Transactional (readOnly = true)
      public List<PostResponse> getAllPosts(){
        return       postRepository.findAll().stream()
                    .map(postMapper::mapToDto)
                    .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPostsByForumId (long forumId){
        Forum forum  = forumRepository.findById(forumId).orElseThrow(()-> new ForumNotFoundException("Not found"));
        List<Post> postlist = postRepository.findAllByForum(forum);
        return postlist.stream().map(postMapper :: mapToDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPostsByForumName (String forumName){
        Forum forum  = forumRepository.findByName(forumName).orElseThrow(()-> new ForumNotFoundException("Not found"));
        List<Post> postlist = postRepository.findAllByForum(forum);
        return postlist.stream().map(postMapper :: mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPostsByUserId (Long userId){
        User user = userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("Not found"));
        List <Post> postList = postRepository.findAllByUser(user);
        return postList.stream().map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPostsByUsername (String username){
        User user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Not found"));
        List <Post> postList = postRepository.findAllByUser(user);
        return postList.stream().map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPostsByCurrentUser (){
        User user = authService.getCurrentUser();
        List <Post> postList = postRepository.findAllByUser(user);
        return postList.stream().map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }
    public int getUserLogos(){
        User user = authService.getCurrentUser();
        return user.getLogos();
    }
    public int getUsersLogos(String username){
        User user = userRepository.findByUsername(username).orElseThrow(()-> new ULSceneException("Not found"));
        return user.getLogos();
    }
    @Transactional(readOnly = true)
    public String getUserCreatedDate(){
        User user = authService.getCurrentUser();
        return TimeAgo.using(user.getCreateDate().toEpochMilli());
    }
    @Transactional(readOnly = true)
    public String getUsersCreatedDate(String username){
        User user = userRepository.findByUsername(username).orElseThrow(()-> new ULSceneException("Not found"));
        String time = TimeAgo.using(user.getCreateDate().toEpochMilli());
        return time;
    }

}
