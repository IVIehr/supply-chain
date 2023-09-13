package ir.alimojahed.general.elasticwrapper.util;


import ir.alimojahed.general.elasticwrapper.domain.model.helper.GenericResponse;
import ir.alimojahed.general.elasticwrapper.exception.ProjectException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Ali Mojahed on 9/8/2021
 * @project smart-gas-meter
 **/

@Log4j2
@Component
public class ResponseUtil {


    public static <T> GenericResponse<T> getResponse(T content, Long totalCount) {
        GenericResponse<T> genericResponse = new GenericResponse<>();
        genericResponse.setResult(content);
        genericResponse.setTotalCount(totalCount);
        genericResponse.setStatus(200);
        genericResponse.setReferenceId(UUID.randomUUID().toString());
        return genericResponse;
    }

    public static <T> GenericResponse<T> getResponse(T content) {
        return getResponse(content, null);
    }

    public static GenericResponse<String> getResponseWithDescription(String description) {
        GenericResponse<String> genericResponse = new GenericResponse<>();
        genericResponse.setDescription(description);
        genericResponse.setResult(description);
        genericResponse.setStatus(200);
        genericResponse.setReferenceId(UUID.randomUUID().toString());
        return genericResponse;
    }

    public static GenericResponse<Object> getErrorGenericResponse(ProjectException e) {
        logError(e);

        GenericResponse<Object> genericResponse = new GenericResponse<>();
        genericResponse.setDescription(e.getMessage());
        genericResponse.setHasError(true);
        genericResponse.setResult(null);
        genericResponse.setStatus(e.getStatus().getCode() == 0 ? 200 : e.getStatus().getCode());
        genericResponse.setReferenceId(UUID.randomUUID().toString());
        return genericResponse;

    }

    public static <T> GenericResponse<T> getErrorCustomResponse(T content, int status) {
        GenericResponse<T> response = new GenericResponse<>();
        response.setStatus(status);
        response.setHasError(true);
        response.setResult(content);
        response.setReferenceId(UUID.randomUUID().toString());
        return response;
    }

    private static void logError(ProjectException e) {
        Logger logger = LogManager.getLogger(ResponseUtil.class);
        logger.log(Level.ERROR, "{}", e);
    }

//    public <T> void sendResponse(HttpServletResponse response, HttpStatus status,
//                                 GenericResponse<T> restResponse) throws ProjectException, IOException {
//        setResponseHeaders(response, status);
//        response.getWriter().write(JsonUtility.getWithNoTimestampJson(restResponse));
//    }

//    public void sendProcessErrorResponse(HttpServletRequest request,
//                                         HttpServletResponse response,
//                                         ProjectException e,
//                                         HttpStatus status) throws IOException, ProjectException {
//
//        sendResponse(response, status, getErrorGenericResponse(e));
//    }

//    public void sendAuthenticationException(HttpServletRequest request,
//                                            HttpServletResponse response,
//                                            AuthenticationException e) throws IOException, ProjectException {
//        try {
//            throw new ProjectException(ExceptionStatus.UNAUTHORIZED);
//
//        } catch (ProjectException exception) {
//            sendProcessErrorResponse(request, response, exception, HttpStatus.UNAUTHORIZED);
//        }
//
//    }

//    private void setResponseHeaders(HttpServletResponse response, HttpStatus status) {
//        response.setStatus(status.value());
//        response.setCharacterEncoding("utf-8");
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setHeader("Server", getServer());
//        response.setHeader("X-Powered-By", getServer());
//    }

//    private String getServer() {
//        return " Server v" + SERVER_VERSION + " #" + SERVER_NAME;
//    }


}

