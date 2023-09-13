package ir.alimojahed.general.elasticwrapper.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import ir.alimojahed.general.elasticwrapper.data.hibernate.entity.User;
import ir.alimojahed.general.elasticwrapper.data.hibernate.repository.UserJpaRepository;
import ir.alimojahed.general.elasticwrapper.exception.ExceptionStatus;
import ir.alimojahed.general.elasticwrapper.exception.ProjectException;
import ir.alimojahed.general.elasticwrapper.util.ResponseWriterUtil;
import ir.alimojahed.general.elasticwrapper.util.Util;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ali Mojahed on 10/9/2022
 * @project iso3
 **/
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    public static final String TOKEN_SCHEMA = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";
    private final SecretKey secretKey;
    private final UserJpaRepository userRepository;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   SecretKey secretKey,
                                   UserJpaRepository userRepository) {

        super(authenticationManager);
        this.secretKey = secretKey;
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws IOException, ServletException {

        String authorizationHeader = httpServletRequest.getHeader(TOKEN_HEADER);
        if (Util.isNullOrEmpty(authorizationHeader) /**|| !authorizationHeader.startsWith(TOKEN_SCHEMA)**/) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        String token = authorizationHeader.replace(TOKEN_SCHEMA, "");
        String username = null;
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            username = (String) body.get("username");
            List<Map<String, String>> authorities = (List<Map<String, String>>) body.get("authorities");

            Set<SimpleGrantedAuthority> simpleGrantedAuthorities =
                    authorities.stream()
                            .map(m -> new SimpleGrantedAuthority(m.get("authority")))
                            .collect(Collectors.toSet());


            Optional<User> userOptional = userRepository.getUserByUsername(username);
            if (!userOptional.isPresent()) {
                ResponseWriterUtil.sendProcessErrorResponse(httpServletRequest, httpServletResponse,
                        new ProjectException(ExceptionStatus.INVALID_TOKEN),
                        HttpStatus.UNAUTHORIZED);
                return;
            }


            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userOptional.get(),
                    null,
                    simpleGrantedAuthorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException e) {
            ResponseWriterUtil.sendProcessErrorResponse(httpServletRequest, httpServletResponse,
                    new ProjectException(ExceptionStatus.INVALID_TOKEN),
                    HttpStatus.UNAUTHORIZED);
            return;
        }

        doFilter(httpServletRequest, httpServletResponse, filterChain);
    }
}
