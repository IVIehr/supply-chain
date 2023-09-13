package ir.alimojahed.general.elasticwrapper.domain.service;

import ir.alimojahed.general.elasticwrapper.data.elastic.ElasticSearchQuery;
import ir.alimojahed.general.elasticwrapper.data.elastic.ElasticsearchResult;
import ir.alimojahed.general.elasticwrapper.domain.model.common.CargoState;
import ir.alimojahed.general.elasticwrapper.domain.model.dto.LogDto;
import ir.alimojahed.general.elasticwrapper.domain.model.helper.GenericResponse;
import ir.alimojahed.general.elasticwrapper.domain.model.srv.LogAggregateSrv;
import ir.alimojahed.general.elasticwrapper.util.ResponseUtil;
import ir.alimojahed.general.elasticwrapper.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class LogService {

    @SneakyThrows
    public GenericResponse<String> log(LogDto content) {

        if (Util.isNullOrEmpty(content.getCargoId())) {
            content.setCargoId(UUID.randomUUID().toString().replaceAll("-", ""));
        }

        content.setInsertTime(LocalDateTime.now());
        content.setStateId(content.getCargoState().ordinal());
        content.setState(content.getCargoState().name());

        ElasticSearchQuery.saveLogDtoToElasticsearch(
                content,
                "localhost",
                9200,
                "elastic",
                "password"

        );

        return ResponseUtil.getResponse(content.getCargoId());
    }


    @SneakyThrows
    public GenericResponse<ElasticsearchResult> getAggregation(String id, CargoState startState, CargoState endState) {
        return ResponseUtil.getResponse(
                ElasticSearchQuery.performElasticsearchQuery(
                        id,
                        startState.ordinal(),
                        endState.ordinal(),
                        "localhost",
                        9200,
                        "elastic",
                        "password"
                        ));
    }
}
