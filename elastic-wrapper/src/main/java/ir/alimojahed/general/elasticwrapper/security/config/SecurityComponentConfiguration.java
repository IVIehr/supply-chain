package ir.alimojahed.general.elasticwrapper.security.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

/**
 * @author Ali Mojahed on 10/5/2022
 * @project iso3
 **/
@Configuration
public class SecurityComponentConfiguration {
    private final String JWT_SECRET_KEY;

    public SecurityComponentConfiguration(@Value("${security.token.jwt.secret.key}") String jwt_secret_key) {
        JWT_SECRET_KEY = jwt_secret_key;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes());
    }

}
