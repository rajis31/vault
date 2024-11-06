package org.devtop.resources;

import io.vertx.core.json.JsonObject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.devtop.encryption.AES256;
import org.devtop.entity.KvEntity;
import org.devtop.json.KeyValue;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.annotations.Param;

@Path("")
public class EncryptionResource {
    @ConfigProperty(name="AES_SECRET")
    private String secret; 

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }
    
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "This is a test";
    }
    
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
    @Path("/kv")
    public List<KvEntity> getKv() {
        return KvEntity.listAll();
    }
    
    @POST
    @Path("/kv/insert")
    @Transactional
    public Response createKv(KeyValue kv) {
        try{
            KvEntity kve = new KvEntity();
            kve.key   = kv.key;
            kve.value = AES256.encrypt(kv.value, secret);
            kve.persist();
            return Response.ok(KvEntity.listAll()).build();
        } catch(Exception e){
         return Response.serverError()
                      .entity("Failed to process request: " + e.getMessage())
                      .build();
    }
   
    }
    
    
}

