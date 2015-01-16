package mjh.tm.restapi.messagewriter.json;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import mjh.tm.restapi.resource.RESTConfiguration;
import mjh.tm.service.entity.Project;
import mjh.tm.service.entity.User;

@Provider
@Produces(RESTConfiguration.PROJECT_MEMBERS_JSON)
public class ProjectMemberListJSONWriter implements MessageBodyWriter<Project> {
    
    @Context UriInfo uriInfo;
    
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (Project.class.isAssignableFrom(type) && mediaType.toString().equals(RESTConfiguration.PROJECT_MEMBERS_JSON)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public long getSize(Project t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(Project project, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        DataOutputStream dos = new DataOutputStream(entityStream);
        dos.writeBytes(toJson(project));
        dos.flush();
        dos.close();
    }
    
    public String toJson(Project project) {
        return toBuilder(uriInfo,project).build().toString();
    }

    public JsonObjectBuilder toBuilder(UriInfo uriInfo, Project project) {
        // NOTE: The json structure we create here is very different
        // than the underlying Entity structure.
        // get builder from base class
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("url", uriInfo.getBaseUriBuilder().path(RESTConfiguration.projectMembersUriTemplate).build(project.getName()).toString());

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
