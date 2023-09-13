package ir.alimojahed.general.elasticwrapper.controller;


import ir.alimojahed.general.elasticwrapper.data.elastic.ElasticsearchResult;
import ir.alimojahed.general.elasticwrapper.domain.model.common.CargoState;
import ir.alimojahed.general.elasticwrapper.domain.model.dto.LogDto;
import ir.alimojahed.general.elasticwrapper.domain.model.helper.Endpoint;
import ir.alimojahed.general.elasticwrapper.domain.model.helper.GenericResponse;
import ir.alimojahed.general.elasticwrapper.domain.model.srv.LogAggregateSrv;
import ir.alimojahed.general.elasticwrapper.domain.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Endpoint.API + Endpoint.LOGS)
@RequiredArgsConstructor
public class LoggerController {

    private final LogService logService;

    @PostMapping
    public GenericResponse<String> log(@RequestHeader("Authorization") String token,
                                       @RequestBody LogDto content) {
        return logService.log(content);
    }


    @GetMapping(Endpoint.ID_PATH_PARAM)
    public GenericResponse<ElasticsearchResult> getLogAggregation(@RequestHeader("Authorization") String token,
                                                                  @PathVariable("id") String id,
                                                                  @RequestParam("startState")CargoState startState,
                                                                  @RequestParam("endState") CargoState endState) {
        return logService.getAggregation(id, startState, endState);
    }

}
