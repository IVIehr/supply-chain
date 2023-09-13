package ir.alimojahed.general.elasticwrapper.filter;

import ir.alimojahed.general.elasticwrapper.exception.ExceptionStatus;
import ir.alimojahed.general.elasticwrapper.exception.ProjectException;
import ir.alimojahed.general.elasticwrapper.util.JsonUtil;
import ir.alimojahed.general.elasticwrapper.util.ResponseUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Ali Mojahed on 2/14/2021
 * @project Notinou
 **/

@Component
@Log4j2
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(
                JsonUtil.getStringJson(ResponseUtil.getErrorGenericResponse(new ProjectException(ExceptionStatus.ACCESS_DENIED)))
        );
    }
}
