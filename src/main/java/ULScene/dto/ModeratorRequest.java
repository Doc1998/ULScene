package ULScene.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModeratorRequest {
    String usernameAdding;
    String usernameBeingAdded;
    String forumName;
}
