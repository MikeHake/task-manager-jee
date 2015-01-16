package mjh.tm.restapi.resource;

import java.security.Principal;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import mjh.tm.service.UserService;
import mjh.tm.service.entity.User;
import mjh.tm.service.exception.UserNotFoundException;

@DeclareRoles("USER")
@Stateless
@Path("users")
public class UserResource {

    @Inject
    UserService userController;

    @Resource
    SessionContext sessionContext;

    private void logCallerInfo() {
        try {
            Principal callerPrincipal = sessionContext.getCallerPrincipal();

            System.out.println("UserResource callerName:" + callerPrincipal.getName() + 
                    ", Has Roles: USER:" + sessionContext.isCallerInRole("USER")
                    + ", ADMIN:" + sessionContext.isCallerInRole("ADMIN"));
        } catch (Error e) {
            System.out.println("Exception trying to determine caller details: " + e.getMessage());
        }
    }

    @RolesAllowed("USER")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@Context UriInfo uriInfo) {

        logCallerInfo();

        List<User> users = userController.getAllUsers();
        // Wrap users in a GenericEntity to preserve Type information 
        // so the proper MessageBodyWriter is selected.
        GenericEntity<List<User>> entity = new GenericEntity<List<User>>(users){};
        Response response = Response.ok(entity, RESTConfiguration.OBJECT_JSON).build();
        return response;
    }

    @RolesAllowed("USER")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{username}")
    public Response getUser(@Context UriInfo uriInfo, @PathParam("username") String userName) throws UserNotFoundException {
        User user = userController.getUser(userName);
        return Response.ok(user, RESTConfiguration.OBJECT_JSON).build();
    }
}
