package mjh.tm.restapi.resource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("v1")
public class RESTConfiguration extends Application {
    // Define our own subtype of application/json to ensure our
    // MessageBodyReader's and Writers are used.
    public static final String OBJECT_JSON = "application/object+json";
    public static final String PROJECT_MEMBERS_JSON = "application/projectmembers+json";
    
    // These templates are used in conjunction with UriBuilder.path(template) to build a 
    // fully qualified URL to a specific resource. I would have preferred not to hard code
    // these and instead use the UriBuilder.path(Class resource, String method) method to
    // get the template from the Path-annotated methods in the resource class. I tried that
    // and it did not work the way I was expecting and I could not get the desired result.
    public static final String projectCollectionUriTemplate  = "projects";
    public static final String projectInstanceUriTemplate    = "projects/{projectName}";
    public static final String projectMembersUriTemplate     = "projects/{projectName}/members";
    public static final String taskInstanceUriTemplate       = "{taskId}";
    public static final String taskCollectionUriTemplate     = "tasks";
    public static final String userInstanceUriTemplate       = "users/{userName}";
    public static final String userCollectionUriTemplate     = "users";
}
