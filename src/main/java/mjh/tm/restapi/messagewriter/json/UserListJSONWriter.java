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
import mjh.tm.service.entity.User;

@Provider
@Produces(RESTConfiguration.OBJECT_JSON)
public class UserListJSONWriter implements MessageBodyWriter<List<User>>{

   @Context UriInfo uriInfo;
    
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (mediaType.toString().equals(RESTConfiguration.OBJECT_JSON) &&
                List.class.isAssignableFrom(type) && genericType instanceof ParameterizedType ) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] actualTypeArgs = (parameterizedType.getActualTypeArguments());
            if(actualTypeArgs[0].equals(User.class)){
                return true;
            }
        }
        return false;
    }

    @Override
    public long getSize(List<User> users, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(List<User> users, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        DataOutputStream dos = new DataOutputStream(entityStream);
        dos.writeBytes(toJson(users));
        dos.flush();
        dos.close();
    }
    
    public String toJson(List<User> users) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("url", uriInfo.getBaseUriBuilder().path(RESTConfiguration.userCollectionUriTemplate).build().toString());
        
        JsonArrayBuilder itemsBuilder = Json.createArrayBuilder();
        for (User user : users) {
            JsonObjectBuilder userBuilder = Json.createObjectBuilder();
            userBuilder.add("url", uriInfo.getBaseUriBuilder().path(RESTConfiguration.userInstanceUriTemplate).build(user.getName()).toString());
            userBuilder.add("name", user.getName());

            itemsBuilder.add(userBuilder);
        }
        jsonBuilder.add("items", itemsBuilder);
        
        return jsonBuilder.build().toString();
    }
}
