package ir.alimojahed.general.elasticwrapper.data.elastic;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ElasticsearchResult implements Serializable {
    private double averageTemperature;
    private double averageHumidity;
    private long alertsCount;
    private String startTime;
    private String endTime;
    private long totalCount;

}
