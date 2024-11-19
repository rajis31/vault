package org.devtop.resources;

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


@Path("/auth")
public class AuthResource {
    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/role")
    @RolesAllowed({ "User", "Admin" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloRolesAllowed(@Context SecurityContext ctx){
        HashMap<String, String> properties = new HashMap<>();
        properties.put("role", String.valueOf(ctx.getUserPrincipal()));
        return Response.ok().build(ctx.getUserPrincipal());
    }


}
