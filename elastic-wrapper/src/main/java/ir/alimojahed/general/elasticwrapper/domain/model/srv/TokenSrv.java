package ir.alimojahed.general.elasticwrapper.domain.model.srv;

import ir.alimojahed.general.elasticwrapper.domain.model.common.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Ali Mojahed on 10/4/2022
 * @project iso3
 **/

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TokenSrv implements Serializable {
    private String accessToken;
    private String refreshToken;
    private int expire;
    private Role role;
}
