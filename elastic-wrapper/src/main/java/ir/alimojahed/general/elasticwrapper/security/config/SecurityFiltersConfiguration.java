package ir.alimojahed.general.elasticwrapper.security.config;

import ir.alimojahed.general.elasticwrapper.data.hibernate.repository.UserJpaRepository;
import ir.alimojahed.general.elasticwrapper.filter.MyAuthenticationEntryPoint;
import ir.alimojahed.general.elasticwrapper.security.filter.JwtAuthenticationFilter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import javax.annotation.Priority;
import javax.crypto.SecretKey;

/**
 * @author Ali Mojahed on 10/5/2022
 * @project iso3
 **/

@Log4j2
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, order = Integer.MIN_VALUE + 1000)
@Priority(1)
public class SecurityFiltersConfiguration extends WebSecurityConfigurerAdapter {
    private final SecretKey secretKey;
    private final UserJpaRepository userRepository;
    private final MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    public SecurityFiltersConfiguration(SecretKey secretKey,
                                        UserJpaRepository userRepository,
                                        MyAuthenticationEntryPoint myAuthenticationEntryPoint) {
        this.secretKey = secretKey;
        this.userRepository = userRepository;
        this.myAuthenticationEntryPoint = myAuthenticationEntryPoint;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .anonymous().principal("_ANONYMOUS_")
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(myAuthenticationEntryPoint)
                .and()
//                .addFilterBefore(new LoggerReferenceIdFilter(), MyBasicAuthenticationFilter.class)
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), secretKey, userRepository))
                .authorizeRequests()
                .antMatchers("/login", "/api/auth/**", "/v2/api-docs", "/actuator/**", "/api/users/applications/{id}/documents",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        /*Probably not needed*/ "/swagger.json",
                        "/docs", "/swagger-ui",
                        "/swagger-ui/**",
                        "/api/general/languages", "/api/users/confirm/{id}"/*, "/api/managers/applications/record/**"*/,
                        "/api/general/countries").permitAll()
                .anyRequest()
                .authenticated();

        http.headers().cacheControl();

    }

    @Override
    public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/auth/**", "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**", "/docs/**", "/swagger-ui/**",
                "/actuator/**", "/api/general/languages"/*, "/api/managers/applications/record/**" */,
                "/api/admins/languages/file/**",
                "/api/users/confirm/{id}");

    }
}
