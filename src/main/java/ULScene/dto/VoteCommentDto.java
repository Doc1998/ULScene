package ULScene.dto;

import ULScene.model.VoteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteCommentDto {
    private VoteType voteType;
    private Long commentId;
}