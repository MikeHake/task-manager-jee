package mjh.tm.service.interceptor;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import mjh.tm.service.UserService;
import mjh.tm.service.entity.User;
import mjh.tm.service.exception.UserNotFoundException;

/**
 * Users, passwords and their roles are managed outside of this application. However this application 
 * does need to know user names, so it can associate users with projects. This interceptor
 * is called prior to the execution of any EJB method. It simply looks at the name of the
 * calling user, and ensures we have a database record for that user. 
 *
 */
@Interceptor
public class UserInterceptor {

    @Resource
    SessionContext sessionContext;
    
    @Inject
    UserService userController;
    
    @AroundInvoke
    public Object checkUser(InvocationContext ctx) throws Exception { 
        String callerName = sessionContext.getCallerPrincipal().getName();
        User user;
        try{
            user = userController.getUser(callerName);
        }catch(UserNotFoundException e){
            // There is not a record in the DB for this user, so create one
            user = userController.createUser(callerName);
            System.out.println("UserInterceptor created user: "+user.getName());
        }
        
        return ctx.proceed(); 
    }

}
