package mjh.tm.restapi.messagewriter.adapter;

import javax.ws.rs.core.UriInfo;

/**
 * This class came about when I was trying to figure out how to support
 * REST HATEOAS principles in the API. I needed to provide URL's in the 
 * JSON response objects, but I wanted to keep the data model free of
 * this type of API specific information. I came up with this as a way 
 * to pass the UriInfo and build a URL to the resource.  
 */
public abstract class MessageBodyJSONAdapter {


    protected final UriInfo uriInfo;
    
    // These templates are used in conjunction with UriBuilder.path(template) to build a 
    // fully qualified URL to a specific resource. I would have preferred not to hard code
    // these and instead use the UriBuilder.path(Class resource, String method) method to
    // get the template from the Path-annotated methods in the resource class. I tried that
    // and it did not work the way I was expecting and I could not get the desired result.
    protected static final String projectCollectionUriTemplate  = "projects";
    protected static final String projectInstanceUriTemplate    = "projects/{projectName}";
    protected static final String projectMembersUriTemplate     = "projects/{projectName}/members";
    protected static final String taskInstanceUriTemplate       = "projects/{projectName}/tasks/{taskId}";
    protected static final String taskCollectionUriTemplate     = "projects/{projectName}/tasks";
    protected static final String userInstanceUriTemplate       = "users/{userName}";
    protected static final String userCollectionUriTemplate     = "users";

    public MessageBodyJSONAdapter(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }
    
    public MessageBodyJSONAdapter() {
        uriInfo = null;
    }
    
    /**
     * Sub classes must implement this to return the desired JSON representation.
     * @return String representing the JSON
     */
    public abstract String toJson();
    
}
