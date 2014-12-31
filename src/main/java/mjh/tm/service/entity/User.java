package mjh.tm.service.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Very simple User entity just so we can associate User names with Projects
 * and Tasks. We do not want to get into the business of managing users,
 * passwords, roles in this application so you will note that all we track
 * is the user name. The user passwords and roles are managed outside the 
 * scope of this application.
 */
@Entity
public class User {

    private String name;

    public User() {
    }

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Username:" + name;
    }

}
