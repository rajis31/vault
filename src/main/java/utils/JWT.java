package utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JWT {

    private final String secretKey;

    public JWT(String secretKey) {
        this.secretKey = secretKey;
        System.out.println(secretKey);
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public Claims parseJwtToken(String token){
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return claimsJws.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    public String generateJwt(){

    }
}
