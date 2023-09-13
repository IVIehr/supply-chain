package ir.alimojahed.general.elasticwrapper.domain.model.helper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Ali Mojahed on 10/7/2022
 * @project iso3
 **/

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
public class GenericQuery implements Serializable {
    private String username;
    private String fullName;
    private int size;
    private int offset;
    private String name;
    private long facultyCode;
    private long gradeCode;
    private Boolean activate;
}
