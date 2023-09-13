package ir.alimojahed.general.elasticwrapper.data.hibernate.repository;

import ir.alimojahed.general.elasticwrapper.data.hibernate.entity.User;
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
public class UserJpaRepository {
    private final SessionFactory sessionFactory;


    public UserJpaRepository(@Qualifier("iso3_sf") SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Optional<User> getUserByUsername(Session session, String username) {

        return session.createQuery("select u" +
                                " from User u " +
                                "where " +
                                "u.username = :username ",
                        User.class)
                .setParameter("username", username)
                .uniqueResultOptional();

    }

    public Optional<User> getUserByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        return getUserByUsername(session, username);

    }

    public User save(Session session, User user) {
        session.persist(user);

        return user;
    }

    public boolean hasUserWithUsername(Session session, String username) {
        return getUserByUsername(session, username).isPresent();
    }

    public void delete(Session session, User user) {
        session.delete(user);
    }


    public Optional<User> getByUserId(long id) {
        Session session = sessionFactory.getCurrentSession();

        return getByUserId(session, id);
    }

    public Optional<User> getByUserId(Session session, long id) {

        return session.createQuery("select user from User user where user.id = :id ", User.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }
}
