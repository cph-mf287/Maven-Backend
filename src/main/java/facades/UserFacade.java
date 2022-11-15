package facades;

import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import errorhandling.API_Exception;
import org.mindrot.jbcrypt.BCrypt;
import security.errorhandling.AuthenticationException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lam@cphbusiness.dk
 */
public class UserFacade {
    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User createUser(String username, String password) throws API_Exception {
        return createUser(username, password, new ArrayList<>());
    }

    public User createUser(String username, String password, List<String> roles) throws API_Exception {
        // Encrypt password:
        password = BCrypt.hashpw(password, BCrypt.gensalt());

        // Construct user:
        User user = new User(username, password);

        // Persist user to database:
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            // If any roles are specified...
            if (roles.size() > 0) {
                // Add each role if it exists:
                for (String roleName : roles) {
                    Role role = em.find(Role.class, roleName);
                    if (role == null) {
                        role = new Role(roleName);
                        em.persist(role);
                    }
                    user.addRole(role);
                }
            }
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            throw new API_Exception("Could not create user", 500, e);
        } finally {
            em.close();
        }

        return user;
    }

    public User getVerifiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid username or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public User getUser(String username) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null) {
                throw new AuthenticationException("Faulty token");
            }
        } finally {
            em.close();
        }
        return user;
    }
}
