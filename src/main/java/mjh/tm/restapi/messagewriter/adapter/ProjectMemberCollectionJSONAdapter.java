package mjh.tm.restapi.messagewriter.adapter;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.UriInfo;

import mjh.tm.service.entity.Project;
import mjh.tm.service.entity.User;

/** 
 * Convert the API JSON representation of a Project member collection
 * 
 * Please see comment in parent class as well.
 */
public class ProjectMemberCollectionJSONAdapter extends MessageBodyJSONAdapter {
    private final Project project;

    public ProjectMemberCollectionJSONAdapter(UriInfo uriInfo, Project project) {
        super(uriInfo);
        this.project = project;
    }

    public String toJson() {
        return toBuilder().build().toString();
    }

    protected JsonObjectBuilder toBuilder() {
        // NOTE: The json structure we create here is very different
        // than the underlying Entity structure.
        // get builder from base class
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("url", uriInfo.getBaseUriBuilder().path(projectMembersUriTemplate).build(project.getName()).toString());

        JsonArrayBuilder itemsBuilder = Json.createArrayBuilder();
        // Add all ordinary members with isProjectAdmin=false
        for (User user : project.getTeamMembers()) {
            JsonObjectBuilder userBuilder = Json.createObjectBuilder();
            userBuilder.add("id", user.getName());
            userBuilder.add("isProjectAdmin", false);
            itemsBuilder.add(userBuilder);
        }
        // Add all projectAdmins with isProjectAdmin=true
        for (User user : project.getProjectAdmins()) {
            JsonObjectBuilder adminBuilder = Json.createObjectBuilder();
            adminBuilder.add("id", user.getName());
            adminBuilder.add("isProjectAdmin", true);
            itemsBuilder.add(adminBuilder);
        }

        jsonBuilder.add("items", itemsBuilder);

        return jsonBuilder;
    }
}
