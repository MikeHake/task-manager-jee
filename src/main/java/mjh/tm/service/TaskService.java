package mjh.tm.service;

import java.util.Date;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mjh.tm.service.entity.Project;
import mjh.tm.service.entity.Task;
import mjh.tm.service.entity.TaskStatus;
import mjh.tm.service.entity.User;
import mjh.tm.service.exception.ForbiddenException;
import mjh.tm.service.exception.ProjectNotFoundException;
import mjh.tm.service.exception.UserNotFoundException;
import mjh.tm.service.interceptor.UserInterceptor;

@DeclareRoles("USER")
@Stateless
@Interceptors(UserInterceptor.class)
public class TaskService {
    @PersistenceContext(unitName = "TaskManagerPersistenceUnit")
    protected EntityManager entityManager;
    
    @Inject
    ProjectService projectController;
    
    @Inject
    AuthorizationService authUtil;
    
    @Inject
    UserService userController;
    
    @RolesAllowed("USER")
    public Task createTask(String projectName,String taskTitle, String taskDescription) throws ProjectNotFoundException, ForbiddenException, UserNotFoundException{
        Project project = projectController.getProject(projectName);
        User callingUser = userController.getCallingUser();
        // ensure user is authorized to add task to project. If they are 
        // allowed to see it then they are authorized to add task
        authUtil.checkAuthorizationToSeeProject(project);
        
        Task task = new Task();
        task.setProject(project);
        task.setTitle(taskTitle);
        task.setDescription(taskDescription);
        task.setOwner(callingUser);
        task.setCreatedBy(callingUser);
        task.setStatus(TaskStatus.NEW);
        task.setCreateDate(new Date());
        project.getTasks().add(task);
        entityManager.persist(project);
        return task;
    }

    @RolesAllowed("USER")
    public Task getTask(String projectName, long taskId) throws ProjectNotFoundException, ForbiddenException {
        Task t = entityManager.find(Task.class, taskId);
        return t;
    }

}
