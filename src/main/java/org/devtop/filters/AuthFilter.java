package org.devtop.filters;

import io.jsonwebtoken.Claims;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import org.devtop.annotations.CheckAuthorization;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import utils.JWT;

import java.io.IOException;
import java.lang.annotation.Annotation;

@Provider
public class AuthFilter implements ContainerRequestFilter {

    private boolean checkAuthorization = false;
    private String[] roles;

    @Context
    UriInfo uriInfo;

    @Context
    ResourceInfo resourceInfo;

    @Context
    HttpHeaders headers;

    @ConfigProperty(name = "JWT_SECRET")
    private String jwtSecret;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        Annotation[] annotations = resourceInfo.getResourceMethod().getAnnotations();

        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(CheckAuthorization.class)) {
                checkAuthorization = true;
                System.out.println("Has CheckAuthorization annotation");
                roles = ((CheckAuthorization) annotation).roles();
            }
        }

        if (checkAuthorization) {
            String token = this.parseAuthHeader(headers, "Authorization");
            System.out.println(jwtSecret);

            if (token == null) {
                containerRequestContext.abortWith(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity("Invalid authorization header")
                                .build()
                );
            }

            // Parse token
            JWT jwt = new JWT(jwtSecret);
            Claims claims = jwt.parseJwtToken(token);

            if (claims == null) {
                containerRequestContext.abortWith(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity("Invalid authorization header")
                                .build()
                );
            } else {
                  String role = claims.get("role", String.class);
                  boolean checkRole = false;
                  checkRole = this.checkRole(roles, role);
                  System.out.println(checkRole);
                
            }
            
          
            
            
            

        }
    }

    public String parseAuthHeader(HttpHeaders headers, String property) {
        String header = headers.getHeaderString(property);
        String[] splitHeader = header.split(" ");
        if (splitHeader == null || splitHeader.length < 2) {
            return null;
        } else {
            return splitHeader[1].trim();
        }
    }
    
    private boolean checkRole(String[] roles, String role){
        for(String r: roles){
            if(r.equals(role)){
                return true;
            }
        }
        
        return false;
    }
}
