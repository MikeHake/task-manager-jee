package mjh.tm.restapi.messagewriter.json;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

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

@Provider
@Produces(RESTConfiguration.OBJECT_JSON)
public class ProjectListJSONWriter implements MessageBodyWriter<List<Project>> {
    
    @Context UriInfo uriInfo;
    
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (mediaType.toString().equals(RESTConfiguration.OBJECT_JSON) &&
                List.class.isAssignableFrom(type) && genericType instanceof ParameterizedType ) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] actualTypeArgs = (parameterizedType.getActualTypeArguments());
            if(actualTypeArgs[0].equals(Project.class)){
                return true;
            }
        }
        return false;
    }

    @Override
    public long getSize(List<Project> projectList, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(List<Project> projects, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        DataOutputStream dos = new DataOutputStream(entityStream);
        dos.writeBytes(toJson(projects));
        dos.flush();
        dos.close();
    }
    
    /**
     * Create the JSON representation of a list of projects
     */
    public String toJson(List<Project> projects) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("url", uriInfo.getBaseUriBuilder().path(RESTConfiguration.projectCollectionUriTemplate).build().toString());
        
        // create array of project items
        JsonArrayBuilder itemsBuilder = Json.createArrayBuilder();
        for (Project project : projects) {
            JsonObjectBuilder projectBuilder = Json.createObjectBuilder();
            // Add the url for each project in the list
            projectBuilder.add("url", uriInfo.getBaseUriBuilder().path(RESTConfiguration.projectInstanceUriTemplate).build(project.getName()).toString());
            projectBuilder.add("name", project.getName());
            projectBuilder.add("displayName", project.getDisplayName());
            projectBuilder.add("description", project.getDescription());
            itemsBuilder.add(projectBuilder);
        }
        jsonBuilder.add("items", itemsBuilder);
        return jsonBuilder.build().toString();
    }

}
