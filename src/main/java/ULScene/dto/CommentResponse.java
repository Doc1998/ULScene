package ULScene.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String postName;
    private String text;
    private String userName;
    private Integer voteCount;
    private String duration;
    private boolean upVote;
    private boolean downVote;
}
