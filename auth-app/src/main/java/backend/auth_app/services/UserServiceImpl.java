package backend.auth_app.services;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import backend.auth_app.dtos.UserDto;
import backend.auth_app.entities.Provider;
import backend.auth_app.entities.User;
import backend.auth_app.exceptions.ResourceNotFoundException;
import backend.auth_app.respositories.UserRepository;
import backend.auth_app.utils.UserHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class UserServiceImpl implements UserService {

    
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional 
    public UserDto createUser(UserDto userDto) {
        
        if(userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        if(userRepository.existsByEmail(userDto.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }

        // Extra checks go here, like password strength, etc.

        User user = modelMapper.map(userDto, User.class);

        user.setProvider(userDto.getProvider() != null ? userDto.getProvider() : Provider.LOCAL); // Default to LOCAL if not provided

        // TODO : Assign role to new user 
        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        


        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        // TODO Auto-generated method stub
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, String userId) {
        // TODO Auto-generated method stub
        UUID uId = UserHelper.parseUUID(userId);
        User existingUser = userRepository.findById(uId).orElseThrow( () -> new ResourceNotFoundException("User not found with id: " + userId));
        // We are not going to change email id for this project;
        if(userDto.getName() != null)
            existingUser.setName(userDto.getName());

        // TODO : Change the password updation logic
        if(userDto.getPassword() != null)
            existingUser.setPassword(userDto.getPassword());
        if(userDto.getImage() != null)
            existingUser.setImage(userDto.getImage());
        if(userDto.getProvider() != null)
            existingUser.setProvider(userDto.getProvider());

        if (userDto.getEnable() != null) {
            existingUser.setEnable(userDto.getEnable());
        }
        
        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {

        UUID uId = UserHelper.parseUUID(userId);
        userRepository.findById(uId).orElseThrow( () -> new ResourceNotFoundException("User not found with id: " + userId));
        userRepository.deleteById(uId);
    }

    @Override
    public UserDto getUserById(String userId) {

        UUID uId = UserHelper.parseUUID(userId);
        User user = userRepository.findById(uId).orElseThrow( () -> new ResourceNotFoundException("User not found with id: " + userId));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public Iterable<UserDto> getAllUsers() {
        // TODO Auto-generated method stub
        return userRepository.findAll()
        .stream()
        .map(
            (user) -> modelMapper.map(user, UserDto.class)
        ).toList();
    }
    
}
