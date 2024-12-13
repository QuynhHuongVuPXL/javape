package be.pxl.activity.api;

import be.pxl.activity.dto.UserRegistrationRequest;
import be.pxl.activity.dto.UserUpdateRequest;
import be.pxl.activity.exception.UserAlreadyExistsException;
import be.pxl.activity.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint to register a user
    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRegistrationRequest request) {
        try {
            userService.registerUser(request);
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>("Email already in use", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid or missing input data", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody @Valid UserUpdateRequest request, Principal principal) {
        try {
            userService.updateUserDetails(principal.getName(), request);
            return new ResponseEntity<>("User details updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
