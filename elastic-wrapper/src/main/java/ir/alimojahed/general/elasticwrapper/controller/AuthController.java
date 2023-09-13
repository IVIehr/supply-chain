package ir.alimojahed.general.elasticwrapper.controller;

import ir.alimojahed.general.elasticwrapper.domain.model.helper.Endpoint;
import ir.alimojahed.general.elasticwrapper.domain.model.helper.GenericResponse;
import ir.alimojahed.general.elasticwrapper.domain.model.srv.TokenSrv;
import ir.alimojahed.general.elasticwrapper.domain.service.auth.AuthService;
import ir.alimojahed.general.elasticwrapper.exception.ProjectException;
import org.springframework.web.bind.annotation.*;

/**
 * @author Ali Mojahed on 10/4/2022
 * @project iso3
 **/

@RestController()
@RequestMapping(Endpoint.API + Endpoint.AUTH)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping(Endpoint.LOGIN)
    public GenericResponse<TokenSrv> login(@RequestParam("username") String username,
                                           @RequestParam("password") String password) throws ProjectException {

        return authService.login(username, password);
    }


    @PostMapping(Endpoint.REFRESH)
    public GenericResponse<TokenSrv> refresh(@RequestHeader("refresh") String refreshToken) throws ProjectException {
        return authService.refreshToken(refreshToken);
    }


}
