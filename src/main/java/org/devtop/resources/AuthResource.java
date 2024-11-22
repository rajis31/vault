package org.devtop.resources;


import io.jsonwebtoken.Claims;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import org.devtop.annotations.CheckAuthorization;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import utils.JWT;
import java.util.HashMap;
import java.util.Map;


class TokenParams{
    String role;
    int id;
}


@Path("/auth")
public class AuthResource {

    @ConfigProperty(name = "JWT_SECRET")
    private String jwtSecret;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/role")
    public Response getRole(@Context HttpHeaders headers){
        String[] authHeader = headers.getHeaderString("Authorization").split(" ");
        System.out.println(authHeader[1].trim());
        JWT jwt = new JWT(jwtSecret);
        Claims claims = jwt.parseJwtToken(authHeader[1].trim());
        if(claims != null){
            System.out.println(claims.get("role"));
        }
        return Response.ok("This is the header").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @CheckAuthorization(roles = {"admin", "client"})
    @Path("/token")
    public Response generateToken(TokenParams params){
        JWT jwt = new JWT(jwtSecret);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", params.role);
        claims.put("id", params.id);

        String token = jwt.generateJwt(claims);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Response.ok(data).build();
    }

}
