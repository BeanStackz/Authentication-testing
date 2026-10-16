package backend.auth_app.services;

import backend.auth_app.dtos.UserDto;

public interface UserService {

    // Create User 
    UserDto createUser(UserDto userDto);

    UserDto getUserByEmail(String email);

    UserDto updateUser(UserDto userDto, String userId);

    void deleteUser(String userId);

    UserDto getUserById(String userId);

    Iterable<UserDto> getAllUsers();

}
