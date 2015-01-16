package mjh.tm.restapi.messagewriter.json;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.Json;
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
import mjh.tm.service.entity.Task;

@Provider
@Produces(RESTConfiguration.OBJECT_JSON)
public class TaskJSONWriter implements MessageBodyWriter<Task> {
    
    @Context UriInfo uriInfo;
    
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (Task.class.isAssignableFrom(type) && mediaType.toString().equals(RESTConfiguration.OBJECT_JSON)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public long getSize(Task t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(Task task, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        DataOutputStream dos = new DataOutputStream(entityStream);
        dos.writeBytes(toJson(task));
        dos.flush();
        dos.close();
    }
    
    public String toJson(Task task) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("url", uriInfo.getRequestUriBuilder().path(RESTConfiguration.taskInstanceUriTemplate).build(task.getId()).toString());
      
        jsonBuilder.add("title", task.getTitle());
        jsonBuilder.add("id", task.getId()+"");
        jsonBuilder.add("description", task.getDescription());
        jsonBuilder.add("project", task.getProject().getName());
        jsonBuilder.add("owner", task.getOwner().getName());
        jsonBuilder.add("status", task.getStatus().toString());
        jsonBuilder.add("createDate", task.getCreateDate().toString());
        jsonBuilder.add("createdBy", task.getCreatedBy().getName());
        return jsonBuilder.build().toString();
    }

}
