package mjh.tm.restapi.messagewriter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import mjh.tm.restapi.messagewriter.adapter.MessageBodyJSONAdapter;
import mjh.tm.restapi.resource.RESTConfiguration;

/**
 * See this article:
 * http://alex.vanboxel.be/2010/11/09/evolve-with-messagebodywriter/
 */
@Provider
@Produces(RESTConfiguration.OBJECT_JSON)
public class JSONMessageWriter implements MessageBodyWriter<MessageBodyJSONAdapter> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (MessageBodyJSONAdapter.class.isAssignableFrom(type) && mediaType.toString().equals(RESTConfiguration.OBJECT_JSON)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public long getSize(MessageBodyJSONAdapter t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // This does not seem to actually be used
        return -1;
    }

    @Override
    public void writeTo(MessageBodyJSONAdapter object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        DataOutputStream dos = new DataOutputStream(entityStream);
        // Note: This next line gave me some trouble. Originally I used
        // dos.writeUTF()
        // but I found extra characters were being prepended to the JSON string.
        // Though trial/error I found writeBytes() seems to do what I want.
        dos.writeBytes(object.toJson());
        dos.flush();
        dos.close();
    }
}
