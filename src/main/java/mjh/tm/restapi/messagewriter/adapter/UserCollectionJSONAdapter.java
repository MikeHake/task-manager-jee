package mjh.tm.restapi.messagewriter.adapter;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.UriInfo;

import mjh.tm.service.entity.User;

/** 
 * Convert the API JSON representation of a User Collection
 * 
 * Please see comment in parent class as well.
 */
public class UserCollectionJSONAdapter extends MessageBodyJSONAdapter{

    private final List<User> users;
    
    public UserCollectionJSONAdapter(UriInfo uriInfo, List<User> users) {
        super(uriInfo);
        this.users = users;
    }

    
    public String toJson() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("url", uriInfo.getBaseUriBuilder().path(userCollectionUriTemplate).build().toString());
        
        JsonArrayBuilder itemsBuilder = Json.createArrayBuilder();
        for (User user : users) {
            JsonObjectBuilder userBuilder = Json.createObjectBuilder();
            userBuilder.add("url", uriInfo.getBaseUriBuilder().path(userInstanceUriTemplate).build(user.getName()).toString());
            userBuilder.add("name", user.getName());

            itemsBuilder.add(userBuilder);
        }
        jsonBuilder.add("items", itemsBuilder);
        
        return jsonBuilder.build().toString();
    }
}
