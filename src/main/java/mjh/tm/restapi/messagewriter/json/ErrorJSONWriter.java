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

@Provider
@Produces(RESTConfiguration.OBJECT_JSON)
public class ErrorJSONWriter implements MessageBodyWriter<Exception> {
    
    @Context UriInfo uriInfo;
    
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (Exception.class.isAssignableFrom(type) && mediaType.toString().equals(RESTConfiguration.OBJECT_JSON)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public long getSize(Exception t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(Exception exception, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        DataOutputStream dos = new DataOutputStream(entityStream);
        dos.writeBytes(toJson(exception));
        dos.flush();
        dos.close();
    }
    
    public String toJson(Exception exception) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("error", exception.getMessage());
        return jsonBuilder.build().toString();
    }

}
