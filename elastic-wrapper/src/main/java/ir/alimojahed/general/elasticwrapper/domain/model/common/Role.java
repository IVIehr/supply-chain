package ir.alimojahed.general.elasticwrapper.domain.model.common;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ali Mojahed on 10/5/2022
 * @project iso3
 **/
public enum Role {
    USER, ADMIN;

    public Set<SimpleGrantedAuthority> getAuthorities() {
        Set<Role> permissions = Sets.newHashSet(this);
        if (this == ADMIN) {
            permissions.add(USER);
        }

        return permissions.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());

    }
}
