package ir.alimojahed.general.elasticwrapper.domain.model.srv;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class LogAggregateSrv {
    private int alertCounts;
    private String startTime;
    private String endTime;
}
