package ULScene.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Forum {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @NotBlank(message = "Community name is required")
    private String name;
    @NotBlank(message = "Description is required")
    private String description;
    private String background;
    @OneToMany(fetch = LAZY)
    private List<Post> posts;
    @ManyToMany(fetch = LAZY)
    private List<User> moderators;
    @ManyToMany(fetch = LAZY)
    @Column(name = "members")
    private List<User> users;
    @ManyToMany(fetch = LAZY)
    @Column(name = "bannedUsers")
    private List<User> bannedUsers;
    private Instant createdDate;
    @ManyToOne(fetch = LAZY)
    private User user;
    boolean isPrivate;
}
