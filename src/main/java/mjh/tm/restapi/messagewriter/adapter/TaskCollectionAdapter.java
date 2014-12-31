package mjh.tm.restapi.messagewriter.adapter;

import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.UriInfo;

import mjh.tm.service.entity.Task;

/** 
 * Convert the API JSON representation of a Task collection
 * 
 * Please see comment in parent class as well.
 */
public class TaskCollectionAdapter extends MessageBodyJSONAdapter{

    private final Set<Task> tasks;
    private final String projectName;
    
    public TaskCollectionAdapter(UriInfo uriInfo, String projectName, Set<Task> tasks) {
        super(uriInfo);
        this.tasks = tasks;
        this.projectName = projectName;
    }
    
    public String toJson() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("url", uriInfo.getBaseUriBuilder().path(taskCollectionUriTemplate).build(projectName).toString());
        
        JsonArrayBuilder itemsBuilder = Json.createArrayBuilder();
        for (Task task : tasks) {
            JsonObjectBuilder taskBuilder = Json.createObjectBuilder();
            taskBuilder.add("url", uriInfo.getBaseUriBuilder().path(taskInstanceUriTemplate).build(projectName,task.getId()).toString());
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
