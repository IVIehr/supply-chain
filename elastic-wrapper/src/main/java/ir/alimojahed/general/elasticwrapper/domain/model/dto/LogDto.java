package ir.alimojahed.general.elasticwrapper.domain.model.dto;


import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Schema;
import ir.alimojahed.general.elasticwrapper.domain.model.common.CargoState;
import lombok.Data;
import lombok.NoArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class LogDto implements Serializable {
    private double temperature;
    private double humidity;
    private String cargoId;
    private CargoState cargoState;
    @Schema(hidden = true)
    private String state;
    private String location;
    private boolean alert;

    @ApiParam(hidden = true)
    @Schema(hidden = true)
    private int stateId;

    @ApiParam(hidden = true)
    @Schema(hidden = true)
    private LocalDateTime insertTime;

}
