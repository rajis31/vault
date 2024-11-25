package org.devtop.resources;

import io.jsonwebtoken.Claims;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.devtop.annotations.CheckAuthorization;
import org.devtop.encryption.AES256;
import org.devtop.entity.User;
import org.devtop.json.LoginValue;
import org.devtop.json.RegisterValue;
import org.devtop.json.DeleteUserValue;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import utils.JWT;



@Path("/auth")
public class AuthResource {

    @Inject
    Validator validator;

    @ConfigProperty(name = "AES_SECRET")
    private String aesSecret;

    @ConfigProperty(name = "JWT_SECRET")
    private String jwtSecret;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/role")
    public Response getRole(@Context HttpHeaders headers) {
        String[] authHeader = headers.getHeaderString("Authorization").split(" ");
        System.out.println(authHeader[1].trim());
        JWT jwt = new JWT(jwtSecret);
        Claims claims = jwt.parseJwtToken(authHeader[1].trim());
        if (claims != null) {
            System.out.println(claims.get("role"));
        }
        return Response.ok("This is the header").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @CheckAuthorization(roles = {"admin", "client"})
    @Path("/token")
    public Response generateToken(TokenParams params) {
        JWT jwt = new JWT(jwtSecret);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", params.role);
        claims.put("id", params.id);

        String token = jwt.generateJwt(claims);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Response.ok(data).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @CheckAuthorization(roles = {"admin"})
    @Transactional
    @Path("/register")
    public Response register(RegisterValue params) {

        Set<ConstraintViolation<RegisterValue>> violations = validator.validate(params);
        
        if(!violations.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST)
                 .entity(new Result(violations))
                 .build();
        }

        try {
            System.out.println("Username is " + params.username);
            User userExists = User.find("username", params.username).firstResult();

            if (userExists != null) {
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity(Map.of("success", false, "message", "User already exists", "user", Map.of()))
                        .build();

            }

            User user = new User();
            user.setNickname(params.nickname);
            user.setPassword(AES256.encrypt(params.password, aesSecret));
            user.setUsername(params.username);
            user.setService(params.service);
            user.persist();

            return Response.status(Response.Status.CREATED)
                    .entity(
                            Map.of("success", true,
                                    "message", "User has been created",
                                    "user", Map.of(
                                            "id", user.getId(),
                                            "username", user.getUsername(),
                                            "service", user.getService()
                                    )
                            ))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal error, please try again")
                    .build();
        }

    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Path("/login")
    public Response login(LoginValue params) {

        Set<ConstraintViolation<LoginValue>> violations = validator.validate(params);
        
        if(!violations.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST)
                 .entity(new Result(violations))
                 .build();
        }

        try {

            User userFound = User.find("username", params.username).firstResult();

            if(userFound == null){
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Username or password is wrong")
                        .build();
            }

            if(!AES256.decrypt(userFound.getPassword(), aesSecret).equals(params.getPassword())){
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Username or password is wrong")
                        .build();
            }

            JWT jwt = new JWT(jwtSecret);
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", userFound.getService());
            claims.put("id", userFound.getId());
            String token = jwt.generateJwt(claims);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);

            // Save token in backend
            userFound.setToken(token);
            userFound.persist();

            return Response.status(Response.Status.CREATED)
                    .entity(data)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal error, please try again")
                    .build();
        }
    }



    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Path("/user/delete")
    public Response deleteUser(LoginValue params) {

        Set<ConstraintViolation<LoginValue>> violations = validator.validate(params);

        if(!violations.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new Result(violations))
                    .build();
        }

        try {

            User userFound = User.find("username", params.username).firstResult();

            if(userFound == null){
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Username or password is wrong")
                        .build();
            }

            if(!AES256.decrypt(userFound.getPassword(), aesSecret).equals(params.getPassword())){
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Username or password is wrong")
                        .build();
            }

            JWT jwt = new JWT(jwtSecret);
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", userFound.getService());
            claims.put("id", userFound.getId());
            String token = jwt.generateJwt(claims);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);

            // Save token in backend
            userFound.setToken(token);
            userFound.persist();

            return Response.status(Response.Status.CREATED)
                    .entity(data)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal error, please try again")
                    .build();
        }
    }

    
    public static class Result {

        Result(String message) {
            this.success = true;
            this.message = message;
        }

        Result(Set<? extends ConstraintViolation<?>> violations) {
            this.success = false;
            this.message = violations.stream()
                    .map(cv -> cv.getMessage())
                    .collect(Collectors.joining(", "));
        }

        private String message;
        private boolean success;

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return success;
        }

    }

}
