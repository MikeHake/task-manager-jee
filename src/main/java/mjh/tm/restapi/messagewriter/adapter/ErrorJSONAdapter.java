package mjh.tm.restapi.messagewriter.adapter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

/**
 * Convert exceptions to the JSON format we want the API user to see
 * by following the same pattern we use to convert other API 
 * data objects to JSON.
 *
 */
public class ErrorJSONAdapter extends MessageBodyJSONAdapter{

    private Exception exception;

    public ErrorJSONAdapter(Exception exception) {
        super();
        this.exception = exception;
    }
    
    public String toJson() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("error", exception.getMessage());
        return jsonBuilder.build().toString();
    }
}
