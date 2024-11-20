package org.devtop.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.util.HashMap;
import java.util.Map;


@Path("/auth")
public class AuthResource {

    @Inject
    JsonWebToken jwt;


    @GET
    @Path("/jwt")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getJwt(){
        return Response.ok(jwt).build();
    }

    @GET
    @Path("/role")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloRolesAllowed(){
        try{
            HashMap<String, String> properties = new HashMap<>();
            properties.put("role", jwt.getClaim("role"));
            properties.put("id", jwt.getClaim("id"));
            return Response.ok(properties).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}
