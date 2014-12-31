package mjh.tm.restapi.resource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("v1")
public class RESTConfiguration extends Application {
    // Define our own subtype of application/json to ensure our
    // MessageBodyReader's and Writers are used.
    public static final String OBJECT_JSON = "application/object+json";
}
