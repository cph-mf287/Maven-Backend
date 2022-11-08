package security;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import errorhandling.API_Exception;
import security.errorhandling.AuthenticationException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

@Path("verify")
public class TokenEndpoint {

    @Context
    SecurityContext securityContext;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyToken(String jsonString) throws API_Exception, ParseException, JOSEException, AuthenticationException {
        String token;

        try{
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            token = json.get("token").getAsString();
        }catch (Exception e){
            throw new API_Exception("Malformed JSON suplied",400,e);
        }

        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SharedSecret.getSharedKey());

        if(signedJWT.verify(verifier)){
            if(new Date().getTime()>signedJWT.getJWTClaimsSet().getExpirationTime().getTime()){
                throw new AuthenticationException("Your Token is no longer valid");
            }
        }

        JsonObject responsJson = new JsonObject();

        for(Map.Entry<String, Object> entry : signedJWT.getJWTClaimsSet().getClaims().entrySet()) {
            responsJson.add(entry.getKey(), (JsonElement) entry.getValue());
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }


        return Response.ok(new Gson().toJson(responsJson)).build();
    }

}
