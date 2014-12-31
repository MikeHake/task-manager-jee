package mjh.tm.service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import mjh.tm.service.entity.Project;
import mjh.tm.service.entity.User;
import mjh.tm.service.exception.ForbiddenException;
import mjh.tm.service.exception.IllegalNameException;
import mjh.tm.service.exception.ProjectAlreadyExistsException;
import mjh.tm.service.exception.ProjectNotFoundException;
import mjh.tm.service.exception.UserNotFoundException;
import mjh.tm.service.interceptor.UserInterceptor;

@DeclareRoles({ "USER", "ADMIN" })
@Stateless
@Interceptors(UserInterceptor.class)
public class ProjectService {
    
    @PersistenceContext(unitName = "TaskManagerPersistenceUnit")
    protected EntityManager entityManager;

    @Inject
    UserService userController;
    
    @Inject
    AuthorizationService authUtil;

    @RolesAllowed({ "USER", "ADMIN" })
    public Project getProject(String name) throws ProjectNotFoundException, ForbiddenException {
        // Use entity graph to specify what fields should be fetched eagerly
        EntityGraph<?> eg = entityManager.getEntityGraph("projectFullEntityGraph");
        Map<String,Object> props = new HashMap<String,Object>();
        props.put("javax.persistence.fetchgraph", eg);
        Project project = entityManager.find(Project.class, name, props);
        if (project == null) {
            throw new ProjectNotFoundException("Project not found");
        }

        authUtil.checkAuthorizationToSeeProject(project);
        return project;
    }

    @RolesAllowed("ADMIN")
    public Project createProject(String name, String displayName, String description) throws ProjectAlreadyExistsException, IllegalNameException {
        // see if project already exists
        Project existing = entityManager.find(Project.class, name);
        if (existing != null) {
            throw new ProjectAlreadyExistsException("Can not create project " + name + " because it already exists");
        }
        // Ensure the name is URL friendly
        if(!isNameLegalForURL(name)){
            throw new IllegalNameException("Invalid project name. Not URL friendly:"+name);
        }

        Project p = new Project();
        p.setName(name);
        p.setDisplayName(displayName);
        p.setDescription(description);
        entityManager.persist(p);
        return p;
    }

    /**
     * Change the project displayName and description
     * 
     * @param name
     * @param displayName
     * @param description
     * @throws ProjectNotFoundException
     */
    @RolesAllowed("USER")
    public void modifyProject(String name, String displayName, String description) throws ProjectNotFoundException, ForbiddenException {
        Project project = getProject(name);
        authUtil.checkAuthorizationToModifyProject(project);
        project.setDisplayName(displayName);
        project.setDescription(description);
        entityManager.merge(project);
    }

    @RolesAllowed("ADMIN")
    public void deleteProject(String name) throws ForbiddenException {
        try {
            Project project = getProject(name);
            entityManager.remove(project);
        } catch (ProjectNotFoundException e) {
            // I am choosing to swallow and ignore this exception here
            // because I want this to follow the idempotent principle.
            // I want the project to be deleted, so if its already been
            // deleted Im happy
        }

    }

    @RolesAllowed("USER")
    public List<Project> getAllProjects() {
        TypedQuery<Project> query = null;
        if (authUtil.isCallerInRole("ADMIN")) {
            // Since this user has the ADMIN role we let them see all projects
            query = entityManager.createNamedQuery("findAllProjects", Project.class);
        }else{
            // Only let them see the projects the are a member of
            String callerName = authUtil.getCallerName();
            query = entityManager.createNamedQuery("findProjectsContainingUser", Project.class);
            query.setParameter("userId", callerName);
        }
        // Use entity graph to specify what fields should be fetched eagerly
        EntityGraph<?> eg = entityManager.getEntityGraph("projectPreviewEntityGraph");
        query.setHint("javax.persistence.loadgraph", eg);
        List<Project> allProjects = query.getResultList();
        return allProjects;
    }

    @RolesAllowed("USER")
    public void addMemberToProject(String projectName, String userName) throws ProjectNotFoundException, UserNotFoundException, ForbiddenException {
        // look up project, this will throw exception if not found
        Project project = getProject(projectName);
        authUtil.checkAuthorizationToModifyProject(project);

        // look up user, throw exception if not found
        User user = userController.getUser(userName);

        // add user to project
        project.getTeamMembers().add(user);

        // save project
        entityManager.merge(project);
    }

    @RolesAllowed("USER")
    public void deleteMemberFromProject(String projectName, String userName) throws ProjectNotFoundException, ForbiddenException {
        // look up project, this will throw exception if not found
        Project project = getProject(projectName);
        authUtil.checkAuthorizationToModifyProject(project);

        Iterator<User> i = project.getTeamMembers().iterator();
        while (i.hasNext()) {
            User u = i.next();
            if (u.getName().equals(userName)) {
                i.remove();
                // save project
                entityManager.merge(project);
                break;
            }
        }
    }

    @RolesAllowed("ADMIN")
    public void addAdminToProject(String projectName, String userName) throws ProjectNotFoundException, UserNotFoundException, ForbiddenException {
        // look up project, this will throw exception if not found
        Project project = getProject(projectName);
        // Since only ADMIN role is allowed no additional programmatic security
        // needed

        // look up user, throw exception if not found
        User user = userController.getUser(userName);

        // add user to project
        project.getProjectAdmins().add(user);

        // save project
        entityManager.merge(project);
    }

    @RolesAllowed("ADMIN")
    public void deleteAdminFromProject(String projectName, String userName) throws ProjectNotFoundException, ForbiddenException {
        // look up project, this will throw exception if not found
        Project project = getProject(projectName);
        // Since only ADMIN role is allowed no additional programmatic security needed

        Iterator<User> i = project.getProjectAdmins().iterator();
        while (i.hasNext()) {
            User u = i.next();
            if (u.getName().equals(userName)) {
                i.remove();
                // save project
                entityManager.merge(project);
                break;
            }
        }
    }
    
    /**
     * This is a cool little technique for determining 
     * if a name is valid within a URL. Meaning it does 
     * not contain spaces or special characters
     */
    private boolean isNameLegalForURL(String name){
        try {
            URL url = new URL("http://"+name);
            url.toURI();
            return true;
        } catch (URISyntaxException e) {
           return false;
        } catch (MalformedURLException e) {
            return false;
        }
        
    }

}
