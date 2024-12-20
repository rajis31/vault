package org.devtop.resources;

import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.devtop.annotations.CheckAuthorization;
import org.devtop.encryption.AES256;
import org.devtop.entity.KvEntity;
import org.devtop.entity.UserEntity;
import org.devtop.json.KeyValue;
import org.devtop.json.UpdateKeyValue;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@Path("/kv")
public class KvResource {

    @Inject
    Validator validator;

    @ConfigProperty(name = "AES_SECRET")
    private String secret;


    @GET
    @CheckAuthorization(roles = {"admin"})
    @Path("/all")
    public List<KvEntity> getKv() {
        return KvEntity.findDecrypted(secret);
    }


    @GET
    @CheckAuthorization(roles = {"admin"})
    @Path("/{id}")
    public List<KvEntity> getKvById(@PathParam("id") int id) {
        return KvEntity.findDecryptedById(id, secret);
    }


    @POST
    @Path("/insert")
    @CheckAuthorization(roles = {"admin"})
    @Transactional
    public Response createKv(KeyValue kv) {

        Set<ConstraintViolation<KeyValue>> violations = validator.validate(kv);

        if(!violations.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new KvResource.Result(violations))
                    .build();
        }
        try {
            KvEntity kve = new KvEntity();
            UserEntity user = UserEntity.findById(kv.user_id);
            if (user == null){
                throw new RuntimeException("User does not exist");
            }
            kve.setUser(user);
            kve.key = kv.key;
            kve.value = AES256.encrypt(kv.value, secret);
            kve.persist();
            return Response.ok(KvEntity.listAll()).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to process request: " + e.getMessage())
                    .build();
        }

    }

    @DELETE
    @Path("/{id}")
    @CheckAuthorization(roles = {"admin"})
    @Transactional
    public Response deleteKv(@PathParam("id") int id) {
        try {
            KvEntity kvFound = KvEntity.findById(id);

            if (kvFound != null) {
                kvFound.delete();
                return Response.ok()
                        .entity("Successfully deleted record")
                        .build();
            }

            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Record not found")
                    .build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to process request: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/update")
    @CheckAuthorization(roles = {"admin"})
    @Transactional
    public Response updateKv(UpdateKeyValue kv) {
        Set<ConstraintViolation<UpdateKeyValue>> violations = validator.validate(kv);

        if(!violations.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new KvResource.Result(violations))
                    .build();
        }

        try {
            KvEntity kvFound = KvEntity.findById(kv.id);
            UserEntity user     = UserEntity.findById(kv.user_id);

            if(kvFound == null || user == null){
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Record not found")
                        .build();
            }

            kvFound.key   = kv.key;
            kvFound.value = AES256.encrypt(kv.value,secret);
            kvFound.setUser(user);
            kvFound.persist();
            return Response.ok(kv).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to process request: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/encrypt")
    @CheckAuthorization(roles = {"admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    public KeyValue encrypt(KeyValue kv) {
        kv.encrypted = AES256.encrypt(kv.value, this.secret);
        return kv;
    }

    @POST
    @Path("/decrypt")
    @CheckAuthorization(roles = {"admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    public String decrypt(JsonObject ob) {
        String val = ob.getString("val");
        return AES256.decrypt(val, this.secret);
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
