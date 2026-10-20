package backend.auth_app.security;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import backend.auth_app.entities.Provider;
import backend.auth_app.entities.RefreshToken;
import backend.auth_app.entities.User;
import backend.auth_app.respositories.RefreshTokenRepository;
import backend.auth_app.respositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component 
 
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(OAuthSuccessHandler.class);
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value ("${app.auth.frontend.success-redirect:http://localhost:5173/auth/success}")
    private String frontendSuccessUrl;

    public OAuthSuccessHandler(UserRepository userRepository, JwtService jwtService, CookieService cookieService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.refreshTokenRepository = refreshTokenRepository;
    }



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // Handle successful OAuth2 authentication here
        // For example, you can generate a JWT token and send it in the response
        logger.info("\n\nSucess Oauth2!");
        logger.info("OAuth2 authentication successful for user: " + authentication.getName());
        // You can also redirect the user to a specific page after successful authentication
        // response.sendRedirect("/home"); // Redirect to home page
        

        //username
        //useremail
        //new userCreate
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // identify user
        String registrationId = "unknow";
        if (authentication instanceof OAuth2AuthenticationToken token) {
            registrationId = token.getAuthorizedClientRegistrationId();

        }

        logger.info("OAuth2 registration ID: " + registrationId);
        logger.info("OAuth2 user attributes: " + oauth2User.getAttributes().toString());

        User user;

        switch (registrationId) {
            case "google" -> {
                String googleId = oauth2User.getAttribute("sub").toString();
                String email = oauth2User.getAttribute("email").toString();
                String name = oauth2User.getAttribute("name").toString();
                String picture = oauth2User.getAttribute("picture").toString();

                User newuser = User.builder()
                        .name(googleId)
                        .email(email)
                        .image(picture)
                        .enable(true)
                        .provider(Provider.GOOGLE)
                        .build();

                user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(newuser));


            }
            case "github" -> {
                String githubId = attributeAsString(oauth2User, "id");
                String login = attributeAsString(oauth2User, "login");
                String email = oauth2User.getAttribute("email");
                String name = oauth2User.getAttribute("name");
                String picture = oauth2User.getAttribute("avatar_url");

                if (email == null || email.isBlank()) {
                    email = login + "@users.noreply.github.com";
                }
                if (name == null || name.isBlank()) {
                    name = login;
                }

                User newuser = User.builder()
                        .name(name)
                        .email(email)
                        .image(picture)
                        .enable(true)
                        .provider(Provider.GITHUB)
                        .build();

                user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(newuser));

            }
            default -> {
                logger.warn("Unsupported OAuth2 provider: " + registrationId);
                throw new RuntimeException("Unsupported OAuth2 provider: " + registrationId);
            }
        }


        String jti = UUID.randomUUID().toString();
        RefreshToken refreshTokenOb = RefreshToken.builder()
            .jti(jti)
            .user(user)
            .revoked(false)
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
            .build();

        refreshTokenRepository.save(refreshTokenOb);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOb.getJti());

        cookieService.attachRefreshCookie(response, refreshToken, (int)jwtService.getRefreshTtlSeconds());


        // response.getWriter().write("OAuth2 authentication successful for user: " + authentication.getName());
        response.sendRedirect(frontendSuccessUrl);
        


    }

    private String attributeAsString(OAuth2User oauth2User, String attributeName) {
        Object value = oauth2User.getAttributes().get(attributeName);
        if (value == null) {
            throw new IllegalStateException(
                    "GitHub OAuth response is missing " + attributeName);
        }
        return value.toString();
    }

    
}
