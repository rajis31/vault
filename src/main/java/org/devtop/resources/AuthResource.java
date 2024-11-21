package org.devtop.resources;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;

@Path("/auth")
public class AuthResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/role")
    public Response getRole(@Context HttpHeaders headers){
        String[] authHeader = headers.getHeaderString("Authorization").split(" ");
        System.out.println(authHeader[1].trim());
        return Response.ok("This is the header").build();
    }
}
