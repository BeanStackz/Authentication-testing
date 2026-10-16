package backend.auth_app.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.auth_app.dtos.UserDto;
import backend.auth_app.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(
            @RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(userDto));
    }

    @GetMapping("/get")
    public ResponseEntity<Iterable<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping ("/email/{emailId}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String emailId) {
        return ResponseEntity.ok(userService.getUserByEmail(emailId));
    }

    @DeleteMapping ("/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping ("/update/{userId}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, @PathVariable String userId) {
        return ResponseEntity.ok(userService.updateUser(userDto, userId));
    }

    @GetMapping ("/get/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
    
}