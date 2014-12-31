package mjh.tm.restapi.messagewriter.adapter;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.UriInfo;

import mjh.tm.service.entity.Project;
import mjh.tm.service.entity.User;

/** 
 * Convert the API JSON representation of a Project
 * 
 * Please see comment in parent class as well.
 */
public class ProjectJSONAdapter extends MessageBodyJSONAdapter {
    private final Project project;
    
    public ProjectJSONAdapter(UriInfo uriInfo,Project project) {
        super(uriInfo);
        this.project = project;
    }

    /**
     * Create the JSON representation
     */
    public String toJson() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("url", uriInfo.getBaseUriBuilder().path(projectInstanceUriTemplate).build(project.getName()).toString());
        
        jsonBuilder.add("name", project.getName());
        jsonBuilder.add("displayName", project.getDisplayName());
        jsonBuilder.add("description", project.getDescription());

        // team members
        JsonArrayBuilder memberBuilder = Json.createArrayBuilder();
        for (User user : project.getTeamMembers()) {
            memberBuilder.add(user.getName());
        }
        jsonBuilder.add("teamMembers", memberBuilder);
        
        // project members
        ProjectMemberCollectionJSONAdapter memberTranslator = new ProjectMemberCollectionJSONAdapter(uriInfo, project);
        jsonBuilder.add("teamMembers", memberTranslator.toBuilder());

        return jsonBuilder.build().toString();
    }
}
