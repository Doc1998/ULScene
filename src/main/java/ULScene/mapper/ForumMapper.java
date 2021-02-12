package ULScene.mapper;


import ULScene.dto.ForumDto;
import ULScene.model.Comment;
import ULScene.model.Forum;
import ULScene.model.Post;
import ULScene.model.User;
import ULScene.service.AuthService;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ForumMapper {
    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(forum.getPosts()))")
    @Mapping(target = "numberOfUsers", expression = "java(mapUsers(forum.getUsers()))")
    @Mapping(target = "isPrivate",expression = "java(isPriv(forum.isPrivate()))")
    @Mapping(target = "duration", expression = "java(getDuration(forum))")
    @Mapping(target = "userName", source = "user.username")
    public abstract ForumDto mapForumToDto(Forum forum);

    Integer mapPosts(List<Post> numberOfPosts){return numberOfPosts.size();}
    Integer mapUsers(List<User> userList){return userList.size();}
    boolean isPriv(boolean isPriva){return isPriva;}

    @InheritInverseConfiguration
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")// we set this field when creating the post
    public abstract Forum mapDtoToForum(ForumDto forumDto, User user);

    String getDuration(Forum forum) {
        return TimeAgo.using(forum.getCreatedDate().toEpochMilli());
    }
}
