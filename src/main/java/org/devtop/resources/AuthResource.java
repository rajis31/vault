package org.devtop.resources;


import io.jsonwebtoken.Claims;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import utils.JWT;

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
        System.out.println(jwt.getSecretKey());
        Claims claims = jwt.parseJwtToken(authHeader[1].trim());
        if(claims != null){
            System.out.println(claims.get("role"));
        }
        return Response.ok("This is the header").build();
    }
}
