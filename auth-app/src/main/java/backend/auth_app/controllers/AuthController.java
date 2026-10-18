package backend.auth_app.controllers;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.auth_app.dtos.LoginRequest;
import backend.auth_app.dtos.RefreshTokenRequest;
import backend.auth_app.dtos.TokenResponse;
import backend.auth_app.dtos.UserDto;
import backend.auth_app.entities.RefreshToken;
import backend.auth_app.entities.User;
import backend.auth_app.respositories.RefreshTokenRepository;
import backend.auth_app.respositories.UserRepository;
import backend.auth_app.security.CookieService;
import backend.auth_app.security.JwtService;
import backend.auth_app.services.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;


@RestController 
@AllArgsConstructor 
@RequestMapping ("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final ModelMapper mapper;
    private final CookieService cookieService;



    @PostMapping ("/login")
    public ResponseEntity<TokenResponse> login(
        @RequestBody LoginRequest loginRequest,
        HttpServletResponse response
    ){

        // Authenticate the user and generate a JWT token
        Authentication authentication = authenticate(loginRequest);
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new BadCredentialsException("User not found"));
        if(!user.isEnabled()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Generate a refresh token and save it to the database
        String jti = UUID.randomUUID().toString();
        var refreshTokenOb = RefreshToken.builder()
            .jti(jti)
            .user(user)
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plusMillis(jwtService.getRefreshTtlSeconds()))
            .revoked(false)
            .build();

        // Save the refresh token to the database
        refreshTokenRepository.save(refreshTokenOb);

        // GENERATE ACCESS TOKEN
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOb.getJti());

        // USE COOKIE SERVICE TO SET REFRESH TOKEN IN HTTP ONLY COOKIE
        cookieService.attachRefreshCookie(response, refreshToken, (int)jwtService.getRefreshTtlSeconds());
        cookieService.addNoStoreHeader(response);

        
        TokenResponse tokenResponse = TokenResponse.of(
            accessToken,
            refreshToken,
            jwtService.getAccessTtlSeconds(),
            mapper.map(user, UserDto.class)
        );

        return ResponseEntity.ok(tokenResponse);

    }

    // Access token and refresh token renew 
    @PostMapping ("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
        @RequestBody(required=false) RefreshTokenRequest body,
        HttpServletResponse response,
        HttpServletRequest request
    ){
        String refreshToken = readRefreshTokenFromRequest(body, request).orElseThrow(() -> new BadCredentialsException("Refresh token is missing"));
        
        if(!jwtService.isRefreshToken(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String jti = jwtService.getJtiFromToken(refreshToken);
        UUID userId = jwtService.getUserIdFromToken(refreshToken);
        RefreshToken storedRefreshToken = refreshTokenRepository.findByJti(jti).orElseThrow(() -> new BadCredentialsException("Refresh token not recognized"));

        if(storedRefreshToken.isRevoked() || storedRefreshToken.getExpiresAt().isBefore(Instant.now())){
            throw new BadCredentialsException("Refresh token is revoked or expired");
        }

        if(refreshToken == null || refreshToken.isBlank()){
            throw new BadCredentialsException("Refresh token is missing");
        }

        if(!storedRefreshToken.getUser().getId().equals(userId)){
            throw new BadCredentialsException("Refresh token does not match user");
        }
        // Refresh token rotate
        storedRefreshToken.setRevoked(true);

        String newJti = UUID.randomUUID().toString();
        storedRefreshToken.setJti(newJti);
        refreshTokenRepository.save(storedRefreshToken);
        User user = storedRefreshToken.getUser();

        var newRefreshTokenOb = RefreshToken.builder()
            .jti(newJti)
            .user(user)
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plusMillis(jwtService.getRefreshTtlSeconds()))
            .revoked(false)
            .build();
        refreshTokenRepository.save(newRefreshTokenOb);
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user, newRefreshTokenOb.getJti());

        cookieService.attachRefreshCookie(response, newRefreshToken, (int)jwtService.getRefreshTtlSeconds());
        cookieService.addNoStoreHeader(response);

        return ResponseEntity.ok(TokenResponse.of(
            newAccessToken,
            newRefreshToken,
            jwtService.getAccessTtlSeconds(),
            mapper.map(user, UserDto.class)
        ));

    }


    private Optional<String> readRefreshTokenFromRequest(RefreshTokenRequest body, HttpServletRequest request) {
        // 1. Prefer reading secure http only cookie first, then fallback to request body
        if(request.getCookies() != null){
            Optional<String> fromCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookieService.getRefreshTokenCookieName().equals(cookie.getName()))
                .map(cookie -> cookie.getValue())
                .filter(v -> !v.isBlank())
                .findFirst();
            
            return fromCookie.isPresent() ? fromCookie : Optional.ofNullable(body != null ? body.refreshToken() : null);
            
        }
        // 2. Fallback to request body if cookie is not present
        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return Optional.of(body.refreshToken());
        }

        // 3. Custom Header (if you want to support it)
        String headerToken = request.getHeader("X-Refresh-Token");
        if (headerToken != null && !headerToken.isBlank()) {
            return Optional.of(headerToken);
        }
        throw new UnsupportedOperationException("Unimplemented method 'readRefreshTokenFromRequest'");
    }

    private Authentication authenticate(LoginRequest loginRequest) {
        // Implement your authentication logic here
        // For example, you can use the AuthService to validate the user's credentials
        // and generate a JWT token if the credentials are valid.

        try{
            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.email(),
                    loginRequest.password()
                )
            );
        } catch (Exception e) {
            // Handle authentication failure
            throw new BadCredentialsException("Username or password is incorrect");
        }
    }


    @PostMapping ("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response){
        // Clear the refresh token cookie
        
        readRefreshTokenFromRequest(null, request).ifPresent( token -> {
            try{
                if(jwtService.isRefreshToken(token))
                {
                    String jti = jwtService.getJtiFromToken(token);
                    refreshTokenRepository.findByJti(jti).ifPresent(refreshToken -> {
                        refreshToken.setRevoked(true);
                        refreshTokenRepository.save(refreshToken);
                    });
                }
            }
            catch(JwtException ignored){
                // If the token is invalid, we can ignore it and proceed to clear the cookie
            }

        });

        cookieService.clearRefreshCookie(response);
        cookieService.addNoStoreHeader(response);
        return ResponseEntity.noContent().build();
    }




    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));
    }
    
}
