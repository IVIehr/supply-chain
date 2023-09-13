package ir.alimojahed.general.elasticwrapper.util;


import ir.alimojahed.general.elasticwrapper.domain.model.helper.GenericResponse;
import ir.alimojahed.general.elasticwrapper.exception.JsonProcessException;
import ir.alimojahed.general.elasticwrapper.exception.ProjectException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author Ali Mojahed on 2/14/2021
 * @project Notinou
 **/

@Log4j2
public class ResponseWriterUtil {

    private static void sendResponse(HttpServletResponse response, HttpStatus status,
                                     GenericResponse<Object> restResponse) throws IOException, JsonProcessException {

        response.setStatus(status.value());
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write(JsonUtil.getWithNoTimestampJson(restResponse));
    }

    public static void sendProcessErrorResponse(HttpServletRequest request,
                                                HttpServletResponse response,
                                                ProjectException e,
                                                HttpStatus status) throws IOException, JsonProcessException {
        log.error(e.getMessage());
        sendResponse(response, status, ResponseUtil.getErrorGenericResponse(e));
    }
}
