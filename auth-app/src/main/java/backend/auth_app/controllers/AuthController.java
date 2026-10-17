package backend.auth_app.controllers;

import java.time.Instant;
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
import backend.auth_app.dtos.TokenResponse;
import backend.auth_app.dtos.UserDto;
import backend.auth_app.entities.RefreshToken;
import backend.auth_app.entities.User;
import backend.auth_app.respositories.RefreshTokenRepository;
import backend.auth_app.respositories.UserRepository;
import backend.auth_app.security.JwtService;
import backend.auth_app.services.AuthService;
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



    @PostMapping ("/login")
    public ResponseEntity<TokenResponse> login(
        @RequestBody LoginRequest loginRequest
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
        
        TokenResponse tokenResponse = TokenResponse.of(
            accessToken,
            refreshToken,
            jwtService.getAccessTtlSeconds(),
            mapper.map(user, UserDto.class)
        );

        return ResponseEntity.ok(tokenResponse);

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




    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));
    }
    
}
