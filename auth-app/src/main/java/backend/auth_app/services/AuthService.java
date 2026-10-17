package backend.auth_app.services;

import org.springframework.stereotype.Service;

import backend.auth_app.dtos.UserDto;

@Service 
public interface AuthService {
    
    UserDto registerUser(UserDto userDto);

    // Later Login 

}
