package utils;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

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
            return  null;
        }
    }

    public String generateJwt(Map<String, Object> claims){
        long delta            = 1000 * 60* 60;
        Date now              = new Date();
        Date expiration       = new Date(now.getTime() + delta);
        SecretKey secretkey   = Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));

        JwtBuilder builder = Jwts.builder()
                .claims(claims)
                .expiration(expiration)
                .issuedAt(now)
                .signWith(SignatureAlgorithm.HS256, secretkey);
        return builder.compact();
    }
}
