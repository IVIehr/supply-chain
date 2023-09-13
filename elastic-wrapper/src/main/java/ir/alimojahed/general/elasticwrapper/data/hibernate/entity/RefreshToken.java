package ir.alimojahed.general.elasticwrapper.data.hibernate.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;

/**
 * @author Ali Mojahed on 10/5/2022
 * @project iso3
 **/

@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
@Getter
@Setter
@Table(name = "ISO3_REFRESH_TOKEN")

public class RefreshToken extends JpaBaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "id")
    private User user;

    @Column(name = "TOKEN", nullable = false, unique = true)
    private String token;

    @Column(name = "EXPIRY_DATE", nullable = false)
    private Instant expiryDate;
}
