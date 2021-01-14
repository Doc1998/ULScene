package ULScene.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForumDto {
    private Long id;
    private String name;
    private String description;
    private Integer numberOfPosts;
    private Integer numberOfUsers;
}
