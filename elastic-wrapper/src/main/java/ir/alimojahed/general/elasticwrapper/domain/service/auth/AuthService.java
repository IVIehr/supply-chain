package ir.alimojahed.general.elasticwrapper.domain.service.auth;

import ir.alimojahed.general.elasticwrapper.data.hibernate.entity.RefreshToken;
import ir.alimojahed.general.elasticwrapper.data.hibernate.entity.User;
import ir.alimojahed.general.elasticwrapper.data.hibernate.repository.RefreshTokenRepository;
import ir.alimojahed.general.elasticwrapper.data.hibernate.repository.UserJpaRepository;
import ir.alimojahed.general.elasticwrapper.domain.model.helper.GenericResponse;
import ir.alimojahed.general.elasticwrapper.domain.model.srv.TokenSrv;
import ir.alimojahed.general.elasticwrapper.exception.ExceptionStatus;
import ir.alimojahed.general.elasticwrapper.exception.ProjectException;
import ir.alimojahed.general.elasticwrapper.security.config.jwt.JwtGenerator;
import ir.alimojahed.general.elasticwrapper.util.RefreshTokenUtil;
import ir.alimojahed.general.elasticwrapper.util.ResponseUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Ali Mojahed on 10/5/2022
 * @project iso3
 **/

@Service
public class AuthService {
    private final SessionFactory sessionFactory;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtGenerator jwtGenerator;
    private final int JWT_TOKEN_EXPIRATION;

    public AuthService(@Qualifier("iso3_sf") SessionFactory sessionFactory,
                       RefreshTokenRepository refreshTokenRepository,
                       UserJpaRepository userJpaRepository,
                       PasswordEncoder passwordEncoder,
                       RefreshTokenService refreshTokenService,
                       JwtGenerator jwtGenerator,
                       @Value("${security.token.access.expiry.seconds}") int jwt_token_expiration) {

        this.sessionFactory = sessionFactory;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.jwtGenerator = jwtGenerator;
        JWT_TOKEN_EXPIRATION = jwt_token_expiration;

    }


    private TokenSrv generateToken(Session session, User user) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(session, user);

        String jwtToken = jwtGenerator.generateToken(user);

        return TokenSrv.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .expire(JWT_TOKEN_EXPIRATION)
                .role(user.getRole())
                .build();
    }

    @Transactional(value = "iso3_tm", rollbackFor = Exception.class)
    public GenericResponse<TokenSrv> login(String username, String password) throws ProjectException {
        Session session = sessionFactory.getCurrentSession();
        Optional<User> userOptional = userJpaRepository.getUserByUsername(session, username);

        if (!userOptional.isPresent() || !passwordEncoder.matches(password, userOptional.get().getPassword())) {
            throw new ProjectException(ExceptionStatus.NOT_FOUND, "user not found");
        }

        if (!userOptional.get().isConfirm()) {
            throw new ProjectException(ExceptionStatus.FORBIDDEN, "you must confirm your account first an email sent to your account ");
        }

        return ResponseUtil.getResponse(generateToken(session, userOptional.get()));
    }

    @Transactional(value = "iso3_tm", rollbackFor = Exception.class)
    public GenericResponse<TokenSrv> refreshToken(String refreshToken) throws ProjectException {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.getRefreshTokenByToken(refreshToken);

        if (!refreshTokenOptional.isPresent() || RefreshTokenUtil.isTokenExpired(refreshTokenOptional.get())) {
            throw new ProjectException(ExceptionStatus.INVALID_TOKEN, "invalid refresh token");
        }

        User user = refreshTokenOptional.get().getUser();
        String jwtToken = jwtGenerator.generateToken(user);

        return ResponseUtil.getResponse(
                TokenSrv.builder()
                        .refreshToken(refreshToken)
                        .accessToken(jwtToken)
                        .expire(JWT_TOKEN_EXPIRATION)
                        .role(user.getRole())
                        .build()
        );
    }

}
