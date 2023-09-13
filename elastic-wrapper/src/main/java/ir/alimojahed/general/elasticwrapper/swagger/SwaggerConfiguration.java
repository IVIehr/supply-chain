package ir.alimojahed.general.elasticwrapper.swagger;


import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;


/**
 * @author Ali Mojahed on 2/14/2021
 * @project Notinou
 **/


@Configuration
@EnableSwagger2
@Log4j2
public class SwaggerConfiguration {


    @Value("${swagger.redirect-url}")
    private String SWAGGER_REDIRECT_URL;

    @Bean
    public Docket api() {
        log.info("Swagger Configured");
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .paths(PathSelectors.regex("/error.*").negate()/*Predicates.not(PathSelectors.regex("/error.*")*/)
                .build()
                .securitySchemes(Collections.singletonList(apiKey()))
                .securityContexts(securityContexts())
                .apiInfo(apiEndPointsInfo());

    }

    private ApiInfo apiEndPointsInfo() {

        return new ApiInfoBuilder()
//                .title(SWAGGER_TITLE)
//                .description(SWAGGER_DESCRIPTION)
//                .termsOfServiceUrl(SWAGGER_TERMS_OF_SERVICE)
//                .version(SERVER_VERSION_CODE)
                .build();

    }

    private List<SecurityContext> securityContexts() {

        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .build()
        );

    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];

        authorizationScopes[0] = authorizationScope;

        return Collections.singletonList(new SecurityReference("Authorization", authorizationScopes));

    }

    private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }


    @Bean
    public UiConfiguration uiConfig() {

        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .docExpansion(DocExpansion.LIST)
                .filter(true)
                .build();

    }

    @Bean
    SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
                .additionalQueryStringParams(null)
                .useBasicAuthenticationWithAccessCodeGrant(false)
                .enableCsrfSupport(false)
                .build();
    }

    @ApiIgnore
    @RestController
    @Log4j2
    public static class Home {
        @Value("${swagger.redirect-url}")
        private String SWAGGER_REDIRECT_URL;

        @GetMapping("/docs")
        public ModelAndView help(ModelMap model, HttpServletRequest request) {
            
            String sharp = request.getRequestURI().substring(5);
            String redirect;

            if (StringUtils.isNotEmpty(sharp) && !sharp.equals("/")) {
                redirect = "redirect:" + SWAGGER_REDIRECT_URL + "#" + sharp;
            } else {
                redirect = "redirect:" + SWAGGER_REDIRECT_URL;
            }

            return new ModelAndView(redirect, model);

        }

    }


}
