package mjh.tm.service.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * Project entity. 
 * Note the use of NamedQueries and NamedEntityGraphs
 *
 */
@NamedQueries({
    @NamedQuery(
        name = "findAllProjects", 
        query = "SELECT p FROM Project p"),
    @NamedQuery(
        name = "findProjectsContainingUser", 
        query = "SELECT DISTINCT p FROM Project p JOIN p.teamMembers tm JOIN p.projectAdmins pa WHERE tm.name = :userId OR pa.name = :userId") 
})

@NamedEntityGraphs({
    @NamedEntityGraph(name = "projectPreviewEntityGraph", attributeNodes = { 
        @NamedAttributeNode("name"), 
        @NamedAttributeNode("displayName"),
        @NamedAttributeNode("description") 
    }),
    @NamedEntityGraph(name = "projectFullEntityGraph", attributeNodes = { 
        @NamedAttributeNode("name"), 
        @NamedAttributeNode("displayName"),
        @NamedAttributeNode("description"), 
        @NamedAttributeNode("tasks"), 
        @NamedAttributeNode("teamMembers"),
        @NamedAttributeNode("projectAdmins") }) 
})
@Entity
public class Project {
    private String name;
    private String displayName;
    private String description;
    private Set<Task> tasks = new HashSet<Task>();
    private Set<User> teamMembers = new HashSet<User>();
    private Set<User> projectAdmins = new HashSet<User>();

    public Project() {
    }

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROJECT_MEMBER", joinColumns = @JoinColumn(name = "PROJECT_NAME", referencedColumnName = "NAME"))
    public Set<User> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(Set<User> teamMembers) {
        this.teamMembers = teamMembers;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROJECT_ADMIN", joinColumns = @JoinColumn(name = "PROJECT_NAME", referencedColumnName = "NAME"))
    public Set<User> getProjectAdmins() {
        return projectAdmins;
    }

    public void setProjectAdmins(Set<User> projectAdmins) {
        this.projectAdmins = projectAdmins;
    }

    public boolean isUserTeamMember(String userName) {
        Set<User> all = new HashSet<User>();
        all.addAll(getProjectAdmins());
        all.addAll(getTeamMembers());
        for (User user : all) {
            if (user.getName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUserProjectAdmin(String userName) {
        for (User user : getProjectAdmins()) {
            if (user.getName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

}
