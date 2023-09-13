package ir.alimojahed.general.elasticwrapper.startup;

import ir.alimojahed.general.elasticwrapper.data.hibernate.entity.User;
import ir.alimojahed.general.elasticwrapper.data.hibernate.repository.UserJpaRepository;
import ir.alimojahed.general.elasticwrapper.domain.model.common.Role;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Log4j2
public class UserStartupApplicationRunner implements ApplicationRunner {

    private final UserJpaRepository userJpaRepository;


    private final SessionFactory sessionFactory;
    private final PasswordEncoder passwordEncoder;

    public UserStartupApplicationRunner(UserJpaRepository userJpaRepository,
                                        @Qualifier("iso3_sf") SessionFactory sessionFactory,
                                        PasswordEncoder passwordEncoder) {
        this.userJpaRepository = userJpaRepository;
        this.sessionFactory = sessionFactory;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(value = "iso3_tm", rollbackFor = Exception.class)
    public void run(ApplicationArguments args) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        Optional<User> userOptional = userJpaRepository.getUserByUsername(session, "admin");

        if (!userOptional.isPresent()) {
            log.info("add admin");
            User user = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("SKvYRPewXrDF"))
                    .role(Role.ADMIN)
                    .fullName("ادمین سیستم")
                    .confirm(true)
                    .build();

            userJpaRepository.save(session, user);

        }
    }

}
