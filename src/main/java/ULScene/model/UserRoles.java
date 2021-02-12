package ULScene.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserRoles {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;
    @OneToOne
    @JoinColumn(name = "roleId", referencedColumnName = "id")
    private Roles role;

}
