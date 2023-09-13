package ir.alimojahed.general.elasticwrapper.data.hibernate.repository;

import ir.alimojahed.general.elasticwrapper.data.hibernate.entity.RefreshToken;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Ali Mojahed on 10/5/2022
 * @project iso3
 **/

@Repository
@Transactional(value = "iso3_tm", rollbackFor = Exception.class)
@Log4j2
public class RefreshTokenRepository {
    private final SessionFactory sessionFactory;

    public RefreshTokenRepository(@Qualifier("iso3_sf") SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Optional<RefreshToken> getRefreshTokenByToken(String token) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("select rt " +
                                "from RefreshToken rt " +
                                "left join fetch rt.user " +
                                "where " +
                                "rt.token = :token",
                        RefreshToken.class)
                .setParameter("token", token)
                .uniqueResultOptional();
    }

    public RefreshToken save(Session session, RefreshToken refreshToken) {
        session.persist(refreshToken);

        return refreshToken;
    }

}
