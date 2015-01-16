package mjh.tm.restapi.messagewriter.json;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

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
import mjh.tm.service.entity.Task;

@Provider
@Produces(RESTConfiguration.OBJECT_JSON)
public class TaskListJSONWriter implements MessageBodyWriter<Set<Task>> {
    
    @Context UriInfo uriInfo;
    
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (mediaType.toString().equals(RESTConfiguration.OBJECT_JSON) &&
                Set.class.isAssignableFrom(type) && genericType instanceof ParameterizedType ) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] actualTypeArgs = (parameterizedType.getActualTypeArguments());
            if(actualTypeArgs[0].equals(Task.class)){
                return true;
            }
        }
        return false;
    }

    @Override
    public long getSize(Set<Task> projectList, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(Set<Task> tasks, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        DataOutputStream dos = new DataOutputStream(entityStream);
        dos.writeBytes(toJson(tasks));
        dos.flush();
        dos.close();
    }
    
    public String toJson(Set<Task> tasks) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("url", uriInfo.getRequestUriBuilder().build().toString());
        
        JsonArrayBuilder itemsBuilder = Json.createArrayBuilder();
        for (Task task : tasks) {
            JsonObjectBuilder taskBuilder = Json.createObjectBuilder();
            taskBuilder.add("url", uriInfo.getRequestUriBuilder().path(RESTConfiguration.taskInstanceUriTemplate).build(task.getId()).toString());
            taskBuilder.add("title", task.getTitle());
            taskBuilder.add("id", task.getId()+"");
            taskBuilder.add("project", task.getProject().getName());
            taskBuilder.add("status", task.getStatus().toString());
            itemsBuilder.add(taskBuilder);
        }
        jsonBuilder.add("items", itemsBuilder);
        
        return jsonBuilder.build().toString();
    }

}
