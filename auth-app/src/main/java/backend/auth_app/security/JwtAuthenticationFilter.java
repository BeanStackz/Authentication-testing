package backend.auth_app.security;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import backend.auth_app.respositories.UserRepository;
import backend.auth_app.utils.UserHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
    throws ServletException, IOException
    {

        String header = request.getHeader("Authorization");
        logger.info("\nAuthorization header: " + header);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            // Validate the token and set the authentication in the security context
            // You can use your JwtService to validate the token and extract user details
            try {
                
                if(!jwtService.isAccessToken(token)){
                    // message: "Invalid token type. Expected an access token."
                    filterChain.doFilter(request, response);
                    return;
                } 
                
                Jws<Claims> parse = jwtService.parseToken(token);

                Claims payload = parse.getPayload();

                String userId = payload.getSubject();
                UUID userUUID = UserHelper.parseUUID(userId);
                userRepository.findById(userUUID).ifPresent(user -> {

                    if(user.isEnabled()){
                        // Set the user details in the security context
                        // You can create a custom UserDetails implementation if needed
                        // For example, you can create a CustomUserDetails class that implements UserDetails
                        // and set it in the security context using SecurityContextHolder.getContext().setAuthentication(...)
                        List<SimpleGrantedAuthority> authorities = (user.getRoles() != null) ? user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()) : 
                            List.of();
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user.getEmail(), null, authorities
                        );

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        // Set the authentication in the security context
                        if(SecurityContextHolder.getContext().getAuthentication() == null){
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
            });


            } catch (ExpiredJwtException e) {
                request.setAttribute("error", "Token has expired");
                // e.printStackTrace();

            } catch (MalformedJwtException e) {
                request.setAttribute("error", "Invalid token format");
                // e.printStackTrace();

            } catch (JwtException e) {
                request.setAttribute("error", "Invalid token");
                // e.printStackTrace();

            } catch (IllegalArgumentException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            filterChain.doFilter(request, response);
        } catch (java.io.IOException ex) {
            System.getLogger(JwtAuthenticationFilter.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

    }

    @Override 
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Exclude the login and register endpoints from JWT authentication
        return path.startsWith("/api/v1/auth/login");
    }


    
}
