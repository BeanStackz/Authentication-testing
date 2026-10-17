package backend.auth_app.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import backend.auth_app.entities.User;
import backend.auth_app.respositories.UserRepository;

@Service 
public class CustomerUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomerUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override 
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Implement the logic to load user details from your data source (e.g., database)
        // For example, you can use a UserRepository to fetch the user by username
        // and then create a CustomerUserDetails object with the user's information.

        // Example:
        // User user = userRepository.findByUsername(username);
        // if (user == null) {
        //     throw new UsernameNotFoundException("User not found");
        // }
        // return new CustomerUserDetails(user);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return user;
       
    }

    
    
}
