package mjh.tm.restapi.resource;

import java.util.Set;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import mjh.tm.service.ProjectService;
import mjh.tm.service.TaskService;
import mjh.tm.service.entity.Project;
import mjh.tm.service.entity.Task;
import mjh.tm.service.exception.ForbiddenException;
import mjh.tm.service.exception.ProjectNotFoundException;
import mjh.tm.service.exception.UserNotFoundException;

@DeclareRoles("USER")
@Stateless
@Path("projects")
public class ProjectTaskResource {

    @Inject
    ProjectService projectController;
    
    @Inject
    TaskService taskController;
    
    @RolesAllowed("USER")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{projectName}/tasks")
    public Response createProjectTask(@Context UriInfo uriInfo, @PathParam("projectName") String projectName, JsonObject jsonObject) 
                throws ProjectNotFoundException, ForbiddenException, UserNotFoundException {
        String taskTitle = jsonObject.getString("title");
        String taskDescription = jsonObject.getString("description");
        Task task = taskController.createTask(projectName, taskTitle, taskDescription);
        return Response.created(uriInfo.getRequestUri()).entity(task).type(RESTConfiguration.OBJECT_JSON).build();
    }
    
    @RolesAllowed("USER")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{projectName}/tasks/")
    public Response getAllProjectTasks(@Context UriInfo uriInfo,@PathParam("projectName") String projectName) throws ProjectNotFoundException, ForbiddenException {
        Project project = projectController.getProject(projectName);
        // Wrap projects in a GenericEntity to preserve Type information 
        // so the proper MessageBodyWriter is selected.
        GenericEntity<Set<Task>> entity = new GenericEntity<Set<Task>>(project.getTasks()){};
        return Response.ok(entity, RESTConfiguration.OBJECT_JSON).build();
    }
    
    @RolesAllowed("USER")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{projectName}/tasks/{taskId}")
    public Response getProjectTask(@Context UriInfo uriInfo,@PathParam("projectName") String projectName, @PathParam("taskId") long taskId) throws ProjectNotFoundException, ForbiddenException {
        Task task = taskController.getTask(projectName,taskId);
        return Response.ok(task, RESTConfiguration.OBJECT_JSON).build();
    }
}
