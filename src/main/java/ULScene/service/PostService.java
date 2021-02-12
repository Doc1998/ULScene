package ULScene.service;

import ULScene.dto.PostRequest;
import ULScene.dto.PostResponse;
import ULScene.exceptions.ForumNotFoundException;
import ULScene.exceptions.ULSceneException;
import ULScene.mapper.PostMapper;
import ULScene.model.Forum;
import ULScene.model.Post;
import ULScene.model.User;
import ULScene.respository.ForumRepository;
import ULScene.respository.PostRepository;
import ULScene.respository.UserRepository;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import javafx.geometry.Pos;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
