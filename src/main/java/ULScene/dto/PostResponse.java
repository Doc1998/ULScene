package ULScene.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Long id;
    private String postName;
    private String url;
    private String description;
    private String userName;
    private String forumName;
    private Integer voteCount;
    private Integer commentCount;
    private String duration;
    /*
        duration will be using the github timeago library
        2 kotlin dependencies needed,1 kotlin plugin
        plus the dep for the github lib
     */
}