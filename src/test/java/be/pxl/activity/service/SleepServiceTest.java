package be.pxl.activity.service;

import be.pxl.activity.domain.Sleep;
import be.pxl.activity.domain.User;
import be.pxl.activity.dto.SleepRegistrationRequest;
import be.pxl.activity.dto.SleepResponse;
import be.pxl.activity.exception.NotFoundException;
import be.pxl.activity.repository.SleepRepository;
import be.pxl.activity.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SleepServiceTest {

    @Mock
    private SleepRepository sleepRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SleepService sleepService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerSleep_ShouldSaveSleep() {
        // Arrange
        SleepRegistrationRequest request = new SleepRegistrationRequest();
        request.setDate(LocalDate.now());
        request.setHours(8);
        request.setMinutes(30);

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        // Act
        sleepService.registerSleep(request, "user@example.com");

        // Assert
        ArgumentCaptor<Sleep> sleepCaptor = ArgumentCaptor.forClass(Sleep.class);
        verify(sleepRepository, times(1)).save(sleepCaptor.capture());
        Sleep savedSleep = sleepCaptor.getValue();

        assertEquals(8, savedSleep.getHours());
        assertEquals(30, savedSleep.getMinutes());
        assertEquals(user, savedSleep.getUser());
    }

    @Test
    void registerSleep_ShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        SleepRegistrationRequest request = new SleepRegistrationRequest();
        request.setDate(LocalDate.now());
        request.setHours(8);
        request.setMinutes(30);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> sleepService.registerSleep(request, "user@example.com"));
    }

    @Test
    void getAllSleepRecords_ShouldReturnListOfSleepResponses() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        Sleep sleep1 = new Sleep();
        sleep1.setId(1L);
        sleep1.setDate(LocalDate.now());
        sleep1.setHours(8);
        sleep1.setMinutes(30);
        sleep1.setUser(user);

        Sleep sleep2 = new Sleep();
        sleep2.setId(2L);
        sleep2.setDate(LocalDate.now());
        sleep2.setHours(7);
        sleep2.setMinutes(45);
        sleep2.setUser(user);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(sleepRepository.findByUserEmail("user@example.com")).thenReturn(List.of(sleep1, sleep2));

        // Act
        List<SleepResponse> responses = sleepService.getAllSleepRecords("user@example.com");

        // Assert
        assertEquals(2, responses.size());
        assertEquals(8, responses.get(0).getHours());
        assertEquals(7, responses.get(1).getHours());
    }

    @Test
    void getSleepById_ShouldReturnSleepResponse() {
        // Arrange
        Sleep sleep = new Sleep();
        sleep.setId(1L);
        sleep.setDate(LocalDate.now());
        sleep.setHours(8);
        sleep.setMinutes(30);

        when(sleepRepository.findById(1L)).thenReturn(Optional.of(sleep));

        // Act
        SleepResponse response = sleepService.getSleepById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(8, response.getHours());
        assertEquals(30, response.getMinutes());
    }

    @Test
    void getSleepById_ShouldThrowNotFoundException() {
        // Arrange
        when(sleepRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sleepService.getSleepById(1L));
    }

    @Test
    void updateSleep_ShouldUpdateSleepDetails() {
        // Arrange
        SleepRegistrationRequest request = new SleepRegistrationRequest();
        request.setDate(LocalDate.now());
        request.setHours(9);
        request.setMinutes(0);

        Sleep sleep = new Sleep();
        sleep.setId(1L);
        sleep.setHours(8);
        sleep.setMinutes(30);

        when(sleepRepository.findById(1L)).thenReturn(Optional.of(sleep));

        // Act
        sleepService.updateSleep(1L, request);

        // Assert
        verify(sleepRepository, times(1)).save(sleep);
        assertEquals(9, sleep.getHours());
        assertEquals(0, sleep.getMinutes());
    }

    @Test
    void updateSleep_ShouldThrowNotFoundException() {
        // Arrange
        SleepRegistrationRequest request = new SleepRegistrationRequest();
        request.setDate(LocalDate.now());
        request.setHours(9);
        request.setMinutes(0);

        when(sleepRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sleepService.updateSleep(1L, request));
    }

    @Test
    void deleteSleep_ShouldDeleteSleepWhenExists() {
        // Arrange
        Sleep sleep = new Sleep();
        sleep.setId(1L);

        when(sleepRepository.findById(1L)).thenReturn(Optional.of(sleep));

        // Act
        sleepService.deleteSleep(1L);

        // Assert
        verify(sleepRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteSleep_ShouldThrowNotFoundException() {
        // Arrange
        when(sleepRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sleepService.deleteSleep(1L));
    }
}
