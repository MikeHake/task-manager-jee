package mjh.tm.service;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import mjh.tm.service.entity.Project;
import mjh.tm.service.exception.ForbiddenException;

/**
 * I created this to encapsulate/isolate interaction with SessionContext
 * to one place.
 */
@Stateless
public class AuthorizationService {
    
    @Resource
    SessionContext context;

    /**
     * Do nothing if the user is ADMIN as they always have access.
     * If not admin then verify they are a member of the project and
     * throw exception if not.
     */
    public void checkAuthorizationToSeeProject(Project project) throws ForbiddenException{
        if(!context.isCallerInRole("ADMIN")){
            String callerName = getCallerName();
            if(!project.isUserTeamMember(callerName)){
                throw new ForbiddenException("User is not a member of the project.");
            }
        }
    }
    
    /**
     * Do nothing if the user is ADMIN as they always have access.
     * If not admin then verify they are a project admin on the project and
     * throw exception if not.
     */
    public void checkAuthorizationToModifyProject(Project project) throws ForbiddenException{
        if(!context.isCallerInRole("ADMIN")){
            String callerName = getCallerName();
            if(!project.isUserProjectAdmin(callerName)){
                throw new ForbiddenException("User is not a member of the project.");
            }
        }
    }
    
    public boolean isCallerInRole(String role){
        return context.isCallerInRole(role);
    }
    
    public String getCallerName(){
        String callerName = context.getCallerPrincipal().getName();
        return callerName;
    }
}
