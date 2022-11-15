package security;

import com.google.gson.Gson;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import entities.User;
import errorhandling.API_Exception;
import errorhandling.GenericExceptionMapper;
import facades.UserFacade;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("verify")
public class TokenEndpoint {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    private static final Gson GSON = new Gson();

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyToken(@HeaderParam("x-access-token") String token) throws API_Exception, AuthenticationException {
        System.out.println("Checking token: " + token);
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SharedSecret.getSharedKey());
            if (signedJWT.verify(verifier)) {
                if (new Date().getTime() > signedJWT.getJWTClaimsSet().getExpirationTime().getTime()) {
                    System.out.println("Token is not valid");
                    throw new AuthenticationException("Your token is no longer valid");
                }
            }
            System.out.println("Token is valid");
            String username = signedJWT.getJWTClaimsSet().getSubject();
            User user = USER_FACADE.getUser(username);
            Token newToken = new Token(username, user.getRolesAsStrings());
            System.out.println("Renewed token: " + newToken.serialize());
            return Response.ok(GSON.toJson(newToken.serialize())).build();
        } catch (ParseException |JOSEException | AuthenticationException e) {
            if (e instanceof AuthenticationException) {
                throw (AuthenticationException) e;
            }
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, e);
            throw new API_Exception("Something went wrong...", 500, e);
        }
    }
}
