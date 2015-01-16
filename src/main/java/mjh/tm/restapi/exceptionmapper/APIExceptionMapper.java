package mjh.tm.restapi.exceptionmapper;

import javax.ejb.EJBAccessException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import mjh.tm.restapi.resource.RESTConfiguration;
import mjh.tm.service.exception.ForbiddenException;
import mjh.tm.service.exception.IllegalNameException;
import mjh.tm.service.exception.ProjectAlreadyExistsException;
import mjh.tm.service.exception.ProjectNotFoundException;
import mjh.tm.service.exception.UserNotFoundException;

/**
 * This is an implementation of a JAX-RS ExceptionMapper that will
 * intercept exceptions thrown from a call to a REST endpoint and convert
 * that exception to a Response object. This allows us to handle exceptions
 * in just one place, rather than having exception handling code spread throughout
 * the API methods.
 * 
 * See this article for a great explanation: http://avianey.blogspot.com/2011/12/exception-mapping-jersey.html
 * 
 * Note: I am still undecided if I prefer a single ExceptionMapper like this, or a separate
 * ExceptionMapper for each Type of Exception. I originally had an ExceptionMapper for each
 * Type of Exception, which made for very small and simple ExceptionMappers, but of course
 * then you need to remember to add a new Mapper if a new Type of Exception is introduced.
 * 
 * For now I am going to go with this pattern of a single Mapper than handles all Exceptions,
 * but may change my mind in the future.
 *
 */
@Provider
public class APIExceptionMapper implements ExceptionMapper<Exception>{

    @Override
    public Response toResponse(Exception e) {
        Response.Status statusCode;
        if(e instanceof EJBAccessException){
            statusCode = Response.Status.UNAUTHORIZED;
        }else if(e instanceof ForbiddenException){
            statusCode = Response.Status.FORBIDDEN;
        }else if(e instanceof IllegalNameException){
            statusCode = Response.Status.BAD_REQUEST;
        }else if(e instanceof ProjectAlreadyExistsException){
            statusCode = Response.Status.CONFLICT;
        }else if(e instanceof ProjectNotFoundException){
            statusCode = Response.Status.NOT_FOUND;
        }else if(e instanceof UserNotFoundException){
            statusCode = Response.Status.NOT_FOUND;
        }else{
            // TODO - handle unknown exception better
            System.err.println("APIExceptionMapper handling unknown exception type:"+e);
            statusCode = Response.Status.INTERNAL_SERVER_ERROR;
        }
       
        return Response.status(statusCode).entity(e).type(RESTConfiguration.OBJECT_JSON).build();
    }
}
