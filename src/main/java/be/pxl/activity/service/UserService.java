package be.pxl.activity.service;

import be.pxl.activity.domain.User;
import be.pxl.activity.domain.UserDetails;
import be.pxl.activity.dto.UserRegistrationRequest;
import be.pxl.activity.dto.UserUpdateRequest;
import be.pxl.activity.exception.UserAlreadyExistsException;
import be.pxl.activity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Injected PasswordEncoder

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserRegistrationRequest request) {
        // Check if email is already in use
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        // Create new user entity and save to database
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // Hash the password before saving it
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    public void updateUserDetails(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserDetails userDetails = user.getUserDetails();
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setUser(user);
            user.setUserDetails(userDetails);
        }

        userDetails.setWeightKg(request.getWeight());
        userDetails.setHeightCm(request.getHeight());
        userDetails.setBirthDate(request.getBirthdate());

        userRepository.save(user); // Cascade ensures UserDetails is also saved
    }
}
