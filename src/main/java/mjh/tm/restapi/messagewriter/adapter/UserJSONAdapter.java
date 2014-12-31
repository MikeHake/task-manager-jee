package mjh.tm.restapi.messagewriter.adapter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.UriInfo;

import mjh.tm.service.entity.User;

/** 
 * Convert the API JSON representation of a User Instance
 * 
 * Please see comment in parent class as well.
 */
public class UserJSONAdapter extends MessageBodyJSONAdapter {
    private final User user;
    
    public UserJSONAdapter(UriInfo uriInfo,User user) {
        super(uriInfo);
        this.user = user;
    }
    
    public String toJson() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("url", uriInfo.getBaseUriBuilder().path(userInstanceUriTemplate).build(user.getName()).toString());
        
        jsonBuilder.add("name", user.getName());
        return jsonBuilder.build().toString();
    }
}
