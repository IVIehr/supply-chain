package ir.alimojahed.general.elasticwrapper.domain.model.helper;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Ali Mojahed on 9/8/2021
 * @project smart-gas-meter
 **/

@Getter
@Setter
public class GenericResponse<T> implements Serializable {
    private boolean hasError = false;
    private String description;
    private T result;
    private transient int status = 200;
    private String referenceId;
    private Long totalCount = null;
}