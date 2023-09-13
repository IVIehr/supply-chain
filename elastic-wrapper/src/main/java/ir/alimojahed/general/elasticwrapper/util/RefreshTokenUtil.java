package ir.alimojahed.general.elasticwrapper.util;

import ir.alimojahed.general.elasticwrapper.data.hibernate.entity.RefreshToken;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Ali Mojahed on 10/5/2022
 * @project iso3
 **/
public class RefreshTokenUtil {
    public static String generateRefreshToken() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static boolean isTokenExpired(RefreshToken refreshToken) {
        return refreshToken.getExpiryDate().isBefore(Instant.now());
    }

}
