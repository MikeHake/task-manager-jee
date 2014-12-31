package mjh.tm.restapi.resource;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
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

import mjh.tm.restapi.messagewriter.adapter.ProjectMemberCollectionJSONAdapter;
import mjh.tm.service.ProjectService;
import mjh.tm.service.entity.Project;
import mjh.tm.service.exception.ForbiddenException;
import mjh.tm.service.exception.ProjectNotFoundException;
import mjh.tm.service.exception.UserNotFoundException;

@DeclareRoles({ "USER", "ADMIN" })
@Stateless
@Path("projects")
public class ProjectMemberResource {

    @Inject
    ProjectService projectController;
    
    /**
     * Add team member to project
     * 
     * @return no content
     * @throws ProjectNotFoundException 
     * @throws UserNotFoundException 
     */
    @RolesAllowed({"USER", "ADMIN" })
    @POST
    @PUT
    @Path("{projectName}/members/{userName}")
    public Response addMemberToProject( @PathParam("projectName") String projectName, @PathParam("userName") String userName) throws ProjectNotFoundException, UserNotFoundException, ForbiddenException {
        projectController.addMemberToProject(projectName, userName);
        return Response.status(Status.NO_CONTENT).build();
    }
    
    @RolesAllowed({"USER", "ADMIN" })
    @DELETE
    @Path("{projectName}/members/{userName}")
    public Response deleteMemberFromProject( @PathParam("projectName") String projectName, @PathParam("userName") String userName) throws ProjectNotFoundException, UserNotFoundException, ForbiddenException {
        projectController.deleteMemberFromProject(projectName, userName);
        return Response.status(Status.NO_CONTENT).build();
    }
    
    @RolesAllowed("USER")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{projectName}/members/")
    public Response getAllProjectMembers(@Context UriInfo uriInfo,@PathParam("projectName") String projectName) throws ProjectNotFoundException, ForbiddenException {
        Project project = projectController.getProject(projectName);
        ProjectMemberCollectionJSONAdapter adapter = new ProjectMemberCollectionJSONAdapter(uriInfo, project);
        return Response.ok(adapter, RESTConfiguration.OBJECT_JSON).build();
    }
    
    @RolesAllowed("ADMIN")
    @POST
    @PUT
    @Path("{projectName}/admins/{userName}")
    public Response addAdminToProject( @PathParam("projectName") String projectName, @PathParam("userName") String userName) throws ProjectNotFoundException, UserNotFoundException, ForbiddenException {
        projectController.addAdminToProject(projectName, userName);
        return Response.status(Status.NO_CONTENT).build();
    }
    
    @RolesAllowed("ADMIN")
    @DELETE
    @Path("{projectName}/admins/{userName}")
    public Response deleteAdminFromProject( @PathParam("projectName") String projectName, @PathParam("userName") String userName) throws ProjectNotFoundException, UserNotFoundException, ForbiddenException {
        projectController.deleteAdminFromProject(projectName, userName);
        return Response.status(Status.NO_CONTENT).build();
    }
}
