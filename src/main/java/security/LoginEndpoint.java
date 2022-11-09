package security;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.*;
import facades.UserFacade;

import java.util.logging.Level;
import java.util.logging.Logger;
import entities.User;
import errorhandling.API_Exception;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import security.errorhandling.AuthenticationException;
import errorhandling.GenericExceptionMapper;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

@Path("login")
public class LoginEndpoint {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    private static final Gson GSON = new Gson();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonString) throws API_Exception, AuthenticationException {
        String username;
        String password;
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            username = json.get("username").getAsString();
            password = json.get("password").getAsString();

        } catch (Exception e) {
           throw new API_Exception("Malformed JSON Supplied", 400, e);
        }

        try {
            User user = USER_FACADE.getVeryfiedUser(username, password);
            String token = new Token(username, user.getRolesAsStrings()).toString();
            System.out.println("Token: " + token);
            return Response.ok(GSON.toJson(token)).build();

        } catch (JOSEException | AuthenticationException ex) {
            if (ex instanceof AuthenticationException) {
                throw (AuthenticationException) ex;
            }
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new AuthenticationException("Invalid username or password! Please try again");
    }
}
