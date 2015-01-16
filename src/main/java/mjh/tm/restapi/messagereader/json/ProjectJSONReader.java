package mjh.tm.restapi.messagereader.json;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import mjh.tm.restapi.resource.RESTConfiguration;
import mjh.tm.service.entity.Project;

// TODO - Consider custom MediaType here?
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectJSONReader implements MessageBodyReader<Project>{
    
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == Project.class;
    }

    @Override
    public Project readFrom(Class<Project> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        
        JsonReader jsonReader = Json.createReader(entityStream);
        JsonObject jsonObject = jsonReader.readObject();
        
        Project p = new Project();
        p.setName(jsonObject.getString("name"));
        p.setDisplayName(jsonObject.getString("displayName"));
        p.setDescription(jsonObject.getString("description"));
        
        return p;
        
    }

}
