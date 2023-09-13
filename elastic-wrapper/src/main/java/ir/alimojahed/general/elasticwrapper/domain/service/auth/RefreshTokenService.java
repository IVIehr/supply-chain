package ir.alimojahed.general.elasticwrapper.domain.service.auth;

import ir.alimojahed.general.elasticwrapper.data.hibernate.entity.RefreshToken;
import ir.alimojahed.general.elasticwrapper.data.hibernate.entity.User;
import ir.alimojahed.general.elasticwrapper.data.hibernate.repository.RefreshTokenRepository;
import ir.alimojahed.general.elasticwrapper.util.RefreshTokenUtil;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * @author Ali Mojahed on 10/5/2022
 * @project iso3
 **/

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final int REFRESH_TOKEN_EXPIRY;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               @Value("${security.token.refresh.expiry.seconds}") int REFRESH_TOKEN_EXPIRY) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.REFRESH_TOKEN_EXPIRY = REFRESH_TOKEN_EXPIRY;
    }

    public RefreshToken createRefreshToken(Session session, User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(RefreshTokenUtil.generateRefreshToken())
                .expiryDate(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRY))
                .user(user)
                .build();

        refreshToken = refreshTokenRepository.save(session, refreshToken);

        return refreshToken;
    }
}
