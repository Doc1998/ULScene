package ULScene.mapper;


import ULScene.dto.ForumDto;
import ULScene.model.Forum;
import ULScene.model.Post;
import ULScene.model.User;
import ULScene.service.AuthService;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ForumMapper {
    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(forum.getPosts()))")
    @Mapping(target = "numberOfUsers", expression = "java(mapUsers(forum.getUsers()))")
    ForumDto mapForumToDto(Forum forum);

    default Integer mapPosts(List<Post> numberOfPosts){return numberOfPosts.size();}
    default Integer mapUsers(List<User> userList){return userList.size();}

    @InheritInverseConfiguration
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")// we set this field when creating the post
    Forum mapDtoToForum(ForumDto forumDto, User user);
}
