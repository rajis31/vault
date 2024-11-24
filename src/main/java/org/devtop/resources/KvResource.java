package org.devtop.resources;

import io.vertx.core.json.JsonObject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.devtop.encryption.AES256;
import org.devtop.entity.KvEntity;
import org.devtop.json.KeyValue;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@Path("/kv")
public class KvResource {

    @ConfigProperty(name = "AES_SECRET")
    private String secret;

    @GET
    @Path("/secret")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSecret() {
        return secret;
    }

    @POST
    @Path("/encrypt")
    @Consumes(MediaType.APPLICATION_JSON)
    public KeyValue encrypt(KeyValue kv) {
        kv.encrypted = AES256.encrypt(kv.value, this.secret);
        return kv;
    }

    @POST
    @Path("/decrypt")
    @Consumes(MediaType.APPLICATION_JSON)
    public String decrypt(JsonObject ob) {
        String val = ob.getString("val");
        String decrypted = AES256.decrypt(val, this.secret);
        return decrypted;
    }

    @GET
    @Path("/all")
    public List<KvEntity> getKv() {
        return KvEntity.findDecrypted(secret);
    }

    @POST
    @Path("/insert")
    @Transactional
    public Response createKv(KeyValue kv) {
        try {
            KvEntity kve = new KvEntity();
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

}
