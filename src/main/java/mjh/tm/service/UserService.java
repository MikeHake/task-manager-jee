package mjh.tm.service;

import java.util.List;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import mjh.tm.service.entity.User;
import mjh.tm.service.exception.UserNotFoundException;

@DeclareRoles({ "USER", "PROJECT_ADMIN", "ADMIN" })
@Stateless
public class UserService {

    @PersistenceContext(unitName = "TaskManagerPersistenceUnit")
    protected EntityManager entityManager;
    
    @Inject
    AuthorizationService authUtil;
    
    @RolesAllowed("USER")
    public User getCallingUser() throws UserNotFoundException {
        String callerName = authUtil.getCallerName();
        return getUser(callerName);
    }

    @RolesAllowed("ADMIN")
    public User createUser(String userName) {
        User u = new User();
        u.setName(userName);
        entityManager.persist(u);
        return u;
    }

    @RolesAllowed("ADMIN")
    public void deleteUser(String name) throws UserNotFoundException {
        User user = getUser(name); // will throw here if not exist
        entityManager.remove(user);
    }

    @RolesAllowed({"PROJECT_ADMIN", "ADMIN"})
    public List<User> getAllUsers() {
        // Example of typed query
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> user = criteriaQuery.from(User.class);
        criteriaQuery.select(user);
        TypedQuery<User> typedQuery = entityManager.createQuery(criteriaQuery);
        List<User> allUsers = typedQuery.getResultList();
        return allUsers;
    }

    @RolesAllowed("USER")
    public User getUser(String name) throws UserNotFoundException {
        User u = entityManager.find(User.class, name);
        if(u==null){
            throw new UserNotFoundException("User not found");
        }
        return u;
    }
}
