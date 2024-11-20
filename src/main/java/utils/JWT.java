package utils;

import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.ws.rs.core.HttpHeaders;

public class JWT {
    @Inject
    JWTParser parser;

    public JsonWebToken parse(HttpHeaders headers) throws Exception{
        String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || (!authHeader.startsWith("Bearer ") || !authHeader.startsWith("bearer "))){
            throw  new Exception("Authorization header is invalid");
        }

        String token = authHeader.substring(7);
        return parser.parse(token);
    }
}
