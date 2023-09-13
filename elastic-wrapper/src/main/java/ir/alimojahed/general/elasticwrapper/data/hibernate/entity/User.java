package ir.alimojahed.general.elasticwrapper.data.hibernate.entity;

import ir.alimojahed.general.elasticwrapper.domain.model.common.Role;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

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
@Table(name = "ISO3_USERS")
public class User extends JpaBaseEntity {
    @Column(name = "NAME")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "role")
    private Role role;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Builder.Default
    @Column(name = "CONFIRM", columnDefinition = "boolean default true")
    private boolean confirm = true;
}
