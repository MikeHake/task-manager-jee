package mjh.tm.restapi.messagewriter.adapter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.UriInfo;

import mjh.tm.service.entity.Task;

/** 
 * Convert the API JSON representation of a Task instance
 * 
 * Please see comment in parent class as well.
 */
public class TaskJSONAdapter extends MessageBodyJSONAdapter {
    private final Task task;
    
    public TaskJSONAdapter(UriInfo uriInfo,Task task) {
        super(uriInfo);
        this.task = task;
    }
    
    public String toJson() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("url", uriInfo.getBaseUriBuilder().path(taskInstanceUriTemplate).build(task.getProject().getName(),task.getId()).toString());
      
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
