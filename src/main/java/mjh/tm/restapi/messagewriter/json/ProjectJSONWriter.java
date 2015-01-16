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
@Produces(RESTConfiguration.OBJECT_JSON)
public class ProjectJSONWriter implements MessageBodyWriter<Project> {
    
    @Context UriInfo uriInfo;
    
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (Project.class.isAssignableFrom(type) && mediaType.toString().equals(RESTConfiguration.OBJECT_JSON)) {
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
    
    /**
     * Create the JSON representation
     */
    public String toJson(Project project) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("url", uriInfo.getBaseUriBuilder().path(RESTConfiguration.projectInstanceUriTemplate).build(project.getName()).toString());
        
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
        ProjectMemberListJSONWriter memberTranslator = new ProjectMemberListJSONWriter();
        jsonBuilder.add("teamMembers", memberTranslator.toBuilder(uriInfo, project));

        return jsonBuilder.build().toString();
    }
}
