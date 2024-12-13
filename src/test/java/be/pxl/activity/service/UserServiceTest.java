package be.pxl.activity.service;

import be.pxl.activity.domain.User;
import be.pxl.activity.dto.UserRegistrationRequest;
import be.pxl.activity.dto.UserUpdateRequest;
import be.pxl.activity.exception.UserAlreadyExistsException;
import be.pxl.activity.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationRequest registrationRequest;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Prepare test data
        registrationRequest = new UserRegistrationRequest("John Doe", "john.doe@example.com", "password123");
        updateRequest = new UserUpdateRequest(75.0, 180.0, LocalDate.of(1990, 1, 1));
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedpassword");

        // Act
        userService.registerUser(registrationRequest);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(registrationRequest));
        assertEquals("Email already in use", exception.getMessage());
    }

    @Test
    void testUpdateUserDetails_Success() {
        // Arrange
        String email = "john.doe@example.com";
        User existingUser = new User();
        existingUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(existingUser));

        // Act
        userService.updateUserDetails(email, updateRequest);

        // Assert
        assertNotNull(existingUser.getUserDetails());
        assertEquals(updateRequest.getWeight(), existingUser.getUserDetails().getWeightKg());
        assertEquals(updateRequest.getHeight(), existingUser.getUserDetails().getHeightCm());
        assertEquals(updateRequest.getBirthdate(), existingUser.getUserDetails().getBirthDate());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testUpdateUserDetails_UserNotFound() {
        // Arrange
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUserDetails(email, updateRequest));
        assertEquals("User not found", exception.getMessage());
    }
}
