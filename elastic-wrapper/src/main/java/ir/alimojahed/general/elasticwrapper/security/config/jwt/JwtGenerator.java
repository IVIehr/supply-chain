package ir.alimojahed.general.elasticwrapper.security.config.jwt;

import io.jsonwebtoken.Jwts;

import ir.alimojahed.general.elasticwrapper.data.hibernate.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

/**
 * @author Ali Mojahed on 10/5/2022
 * @project iso3
 **/
@Component
public class JwtGenerator {

    private final int JWT_EXPIRY;
    private final SecretKey secretKey;

    public JwtGenerator(@Value("${security.token.access.expiry.seconds}") int jwt_expiry,
                        SecretKey secretKey) {
        JWT_EXPIRY = jwt_expiry;
        this.secretKey = secretKey;
    }

    public String generateToken(User user) {
        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("authorities", user.getRole().getAuthorities())
                .claim("id", user.getId())
                .claim("username", user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(JWT_EXPIRY)))
                .signWith(secretKey)
                .compact();

        return token;
    }

}
