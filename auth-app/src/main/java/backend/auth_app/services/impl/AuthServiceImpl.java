package backend.auth_app.services.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import backend.auth_app.dtos.UserDto;
import backend.auth_app.services.AuthService;
import backend.auth_app.services.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor 
@Service 
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDto registerUser(UserDto userDto) {

        // Logic like verifying email, password strength, etc. can be added here before creating the user.
        // Default role assignment or other business logic can also be handled here.
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userService.createUser(userDto);

    }
}
