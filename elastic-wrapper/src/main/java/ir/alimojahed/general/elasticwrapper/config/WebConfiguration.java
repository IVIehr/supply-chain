package ir.alimojahed.general.elasticwrapper.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by a.rokni on 2020/08/13 @Podspace.
 */

@Log4j2
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Value("${web.config.cors.mapping.path.pattern}")
    private String PATH_PATTERN;

    @Value("${web.config.cors.mapping.methods.allowed}")
    private String[] ALLOWED_METHODS;

    @Value("${web.config.cors.mapping.origins.allowed}")
    private String[] ALLOWED_ORIGINS;


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("configure cors mapping");
        registry.addMapping(PATH_PATTERN)
                .allowedMethods(ALLOWED_METHODS)
                .allowedOrigins(ALLOWED_ORIGINS);


    }
}
