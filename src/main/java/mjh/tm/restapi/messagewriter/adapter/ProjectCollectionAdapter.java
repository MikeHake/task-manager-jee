package mjh.tm.restapi.messagewriter.adapter;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.UriInfo;

import mjh.tm.service.entity.Project;

/** 
 * Convert the API JSON representation of a Project collection
 * 
 * Please see comment in parent class as well.
 */
public class ProjectCollectionAdapter extends MessageBodyJSONAdapter{

    private final List<Project> projects;
    
    public ProjectCollectionAdapter(UriInfo uriInfo, List<Project> projects) {
        super(uriInfo);
        this.projects = projects;
    }
    
    
    /**
     * Create the JSON representation of a list of projects
     */
    public String toJson() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("url", uriInfo.getBaseUriBuilder().path(projectCollectionUriTemplate).build().toString());
        
        // create array of project items
        JsonArrayBuilder itemsBuilder = Json.createArrayBuilder();
        for (Project project : projects) {
            JsonObjectBuilder projectBuilder = Json.createObjectBuilder();
            // Add the url for each project in the list
            projectBuilder.add("url", uriInfo.getBaseUriBuilder().path(projectInstanceUriTemplate).build(project.getName()).toString());
            projectBuilder.add("name", project.getName());
            projectBuilder.add("displayName", project.getDisplayName());
            projectBuilder.add("description", project.getDescription());
            itemsBuilder.add(projectBuilder);
        }
        jsonBuilder.add("items", itemsBuilder);
        return jsonBuilder.build().toString();
    }
    
}
