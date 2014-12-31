package mjh.tm.restapi.resource;

import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import mjh.tm.restapi.messagewriter.adapter.ProjectCollectionAdapter;
import mjh.tm.restapi.messagewriter.adapter.ProjectJSONAdapter;
import mjh.tm.service.ProjectService;
import mjh.tm.service.TaskService;
import mjh.tm.service.entity.Project;
import mjh.tm.service.exception.ForbiddenException;
import mjh.tm.service.exception.IllegalNameException;
import mjh.tm.service.exception.ProjectAlreadyExistsException;
import mjh.tm.service.exception.ProjectNotFoundException;

@DeclareRoles({ "USER", "ADMIN" })
@Stateless
@Path("projects")
public class ProjectResource {

    @Inject
    ProjectService projectController;
    
    @Inject
    TaskService taskController;

    @Resource
    SessionContext sessionContext;

    /**
     * Create a new project and return the URI location to that new user
     * 
     * @return newly created project
     * @throws ProjectAlreadyExistsException 
     */
    @RolesAllowed("ADMIN")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProject(@Context UriInfo uriInfo, JsonObject jsonObject) throws ProjectAlreadyExistsException, IllegalNameException{
        String name = jsonObject.getString("name");
        String displayName = jsonObject.getString("displayName");
        String description = jsonObject.getString("description");
        Project project = projectController.createProject(name, displayName, description);
        ProjectJSONAdapter adapter = new ProjectJSONAdapter(uriInfo,project);
        return Response.created(uriInfo.getRequestUri()).entity(adapter).type(RESTConfiguration.OBJECT_JSON).build();
    }
    
    /**
     * Modify project and return 
     * 
     * @return nothing
     * @throws ProjectNotFoundException 
     * @throws ForbiddenException 
     */
    @RolesAllowed("USER")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{projectName}")
    public Response modifyProject(@Context UriInfo uriInfo, @PathParam("projectName") String projectName, JsonObject jsonObject) 
            throws ProjectNotFoundException, ForbiddenException {
        String displayName = jsonObject.getString("displayName");
        String description = jsonObject.getString("description");
        projectController.modifyProject(projectName, displayName, description);
        return Response.status(Status.NO_CONTENT).build();
    }
    
    /**
     * Delete project
     * 
     * @param projectName
     * @return
     * @throws ProjectNotFoundException 
     * @throws ForbiddenException 
     */
    @RolesAllowed("ADMIN")
    @DELETE
    @Path("{projectName}")
    public Response deleteProject(@PathParam("projectName") String projectName) throws ProjectNotFoundException, ForbiddenException {
        projectController.deleteProject(projectName);
        return Response.status(Status.NO_CONTENT).build();
    }

    @RolesAllowed("USER")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProjects(@Context UriInfo uriInfo) {
        List<Project> projects = projectController.getAllProjects();
        ProjectCollectionAdapter adapter = new ProjectCollectionAdapter(uriInfo, projects);
        Response response = Response.ok(adapter, RESTConfiguration.OBJECT_JSON).build();
        return response;
    }
    
    @RolesAllowed("USER")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{projectName}")
    public Response getProject(@Context UriInfo uriInfo, @PathParam("projectName") String projectName) throws ProjectNotFoundException, ForbiddenException {
        Project project = projectController.getProject(projectName);
        ProjectJSONAdapter adapter = new ProjectJSONAdapter(uriInfo,project);
        return Response.ok(adapter, RESTConfiguration.OBJECT_JSON).build();
    }
    
}
