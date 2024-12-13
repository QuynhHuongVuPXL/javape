package be.pxl.activity.service;

import be.pxl.activity.domain.Activity;
import be.pxl.activity.domain.Distance;
import be.pxl.activity.domain.Unit;
import be.pxl.activity.domain.User;
import be.pxl.activity.dto.ActivityRegistrationRequest;
import be.pxl.activity.dto.ActivityStatisticsResponse;
import be.pxl.activity.repository.ActivityRepository;
import be.pxl.activity.repository.UserRepository;
import be.pxl.activity.exception.ForbiddenActionException;
import be.pxl.activity.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ActivityService activityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerActivity_validRequest_activityRegistered() {
        // Arrange
        String email = "user@example.com";
        ActivityRegistrationRequest request = new ActivityRegistrationRequest();
        request.setActivity("Running");
        request.setStart(LocalDateTime.now().minusHours(1));
        request.setEnd(LocalDateTime.now());
        request.setDistance(new Distance(5, Unit.KM));

        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(activityRepository.save(any(Activity.class))).thenReturn(new Activity());

        // Act
        activityService.registerActivity(request, email);

        // Assert
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    void registerActivity_startTimeAfterEndTime_throwsIllegalArgumentException() {
        // Arrange
        String email = "user@example.com";
        ActivityRegistrationRequest request = new ActivityRegistrationRequest();
        request.setActivity("Running");
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().minusHours(1));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> activityService.registerActivity(request, email));
    }

    @Test
    void deleteActivity_activityNotFound_throwsNotFoundException() {
        // Arrange
        long activityId = 1L;
        String principalName = "user@example.com";
        when(activityRepository.existsById(activityId)).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> activityService.deleteActivity(activityId, () -> principalName));
    }

    @Test
    void deleteActivity_userNotOwner_throwsForbiddenActionException() {
        // Arrange
        long activityId = 1L;
        String principalName = "user@example.com";
        User user = new User();
        user.setEmail("otheruser@example.com");

        Activity activity = new Activity();
        activity.setUser(user);

        when(activityRepository.existsById(activityId)).thenReturn(true);
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

        // Act & Assert
        assertThrows(ForbiddenActionException.class, () -> activityService.deleteActivity(activityId, () -> principalName));
    }

    @Test
    void deleteActivity_activityDeleted_success() {
        // Arrange
        long activityId = 1L;
        String principalName = "user@example.com";
        User user = new User();
        user.setEmail(principalName);

        Activity activity = new Activity();
        activity.setUser(user);

        when(activityRepository.existsById(activityId)).thenReturn(true);
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

        // Act
        activityService.deleteActivity(activityId, () -> principalName);

        // Assert
        verify(activityRepository, times(1)).deleteById(activityId);
    }

    @Test
    void getSportTotals_noActivities_returnNoContent() {
        // Arrange
        String selectedSport = "Running";
        String email = "user@example.com";
        when(activityRepository.findByUserEmailAndSport(email, selectedSport)).thenReturn(List.of());

        // Act
        ResponseEntity<Object> response = activityService.getSportTotals(selectedSport, email);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void getActivityStatistics_validRequest_returnStatistics() {
        // Arrange
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        Activity activity1 = new Activity();
        activity1.setCaloriesBurned(123.2F);
        activity1.setActivity("rowing");
        activity1.setUser(user);
        activity1.setDuration("22 minutes 25 seconds");
        activity1.setStartTime(LocalDateTime.of(2024, 11, 20, 8, 30));
        activity1.setDate(LocalDate.of(2024, 11, 20));

        Activity activity2 = new Activity();
        activity2.setCaloriesBurned(150.0F);
        activity2.setActivity("cycling");
        activity2.setUser(user);
        activity2.setDuration("45 minutes 30 seconds");
        activity2.setStartTime(LocalDateTime.of(2024, 11, 20, 10, 30));
        activity2.setDate(LocalDate.of(2024, 11, 20));

        // set the mock to return the list of activities
        when(activityRepository.findByUserEmail(email)).thenReturn(List.of(activity1, activity2));

        // Act
        ActivityStatisticsResponse response = activityService.getActivityStatistics(email);

        // Assert
        // validate the statistics
        assertEquals(2, response.getTotalActivities());
        assertEquals(273.2, response.getTotalCalories(), 0.1);

        // validate the activity counts per type
        assertTrue(response.getActivityCount().containsKey("rowing"));
        assertTrue(response.getActivityCount().containsKey("cycling"));
        assertEquals(1, response.getActivityCount().get("rowing"));
        assertEquals(1, response.getActivityCount().get("cycling"));

        // heaviest activity
        assertEquals("cycling", response.getHeaviestActivity().getActivity());
        assertEquals(150.0F, response.getHeaviestActivity().getCaloriesBurned(), 0.01);

        // longest activity
        assertEquals("cycling", response.getLongestActivity().getActivity());
        assertEquals("45 minutes 30 seconds", response.getLongestActivity().getDuration());

        // earliest and latest times
        assertNotNull(response.getEarliest());
        assertNotNull(response.getLatest());
    }



}
