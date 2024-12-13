package be.pxl.activity.service;

import be.pxl.activity.domain.Meal;
import be.pxl.activity.domain.User;
import be.pxl.activity.dto.MealRegistrationRequest;
import be.pxl.activity.dto.MealResponse;
import be.pxl.activity.exception.NotFoundException;
import be.pxl.activity.repository.MealRepository;
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

class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MealService mealService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerMeal_ShouldSaveMeal() {
        // Arrange
        MealRegistrationRequest request = new MealRegistrationRequest();
        request.setName("Test Meal");
        request.setDate(LocalDate.now());

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        // Act
        mealService.registerMeal(request, "user@example.com");

        // Assert
        ArgumentCaptor<Meal> mealCaptor = ArgumentCaptor.forClass(Meal.class);
        verify(mealRepository, times(1)).save(mealCaptor.capture());
        Meal savedMeal = mealCaptor.getValue();

        assertEquals("Test Meal", savedMeal.getName());
        assertEquals(user, savedMeal.getUser());
    }

    @Test
    void registerMeal_ShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        MealRegistrationRequest request = new MealRegistrationRequest();
        request.setName("Test Meal");
        request.setDate(LocalDate.now());

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> mealService.registerMeal(request, "user@example.com"));
    }

    @Test
    void getMealById_ShouldReturnMealResponse() {
        // Arrange
        Meal meal = new Meal();
        meal.setId(1L);
        meal.setName("Test Meal");
        meal.setDate(LocalDate.now());
        meal.setCalories(100);

        when(mealRepository.findById(1L)).thenReturn(Optional.of(meal));

        // Act
        MealResponse response = mealService.getMealById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Meal", response.getName());
    }

    @Test
    void getMealById_ShouldThrowNotFoundException() {
        // Arrange
        when(mealRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> mealService.getMealById(1L));
    }

    @Test
    void getAllMealsForUser_ShouldReturnListOfMealResponses() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        Meal meal1 = new Meal();
        meal1.setId(1L);
        meal1.setName("Meal 1");
        meal1.setDate(LocalDate.now());
        meal1.setCalories(100);
        meal1.setUser(user);

        Meal meal2 = new Meal();
        meal2.setId(2L);
        meal2.setName("Meal 2");
        meal2.setDate(LocalDate.now());
        meal2.setCalories(200);
        meal2.setUser(user);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(mealRepository.findAll()).thenReturn(List.of(meal1, meal2));

        // Act
        List<MealResponse> responses = mealService.getAllMealsForUser("user@example.com");

        // Assert
        assertEquals(2, responses.size());
        assertEquals("Meal 1", responses.get(0).getName());
        assertEquals("Meal 2", responses.get(1).getName());
    }

    @Test
    void updateMeal_ShouldUpdateMealDetails() {
        // Arrange
        MealRegistrationRequest request = new MealRegistrationRequest();
        request.setName("Updated Meal");
        request.setDate(LocalDate.now());

        Meal meal = new Meal();
        meal.setId(1L);
        meal.setName("Old Meal");

        when(mealRepository.findById(1L)).thenReturn(Optional.of(meal));

        // Act
        mealService.updateMeal(1L, request);

        // Assert
        verify(mealRepository, times(1)).save(meal);
        assertEquals("Updated Meal", meal.getName());
    }

    @Test
    void updateMeal_ShouldThrowNotFoundException() {
        // Arrange
        MealRegistrationRequest request = new MealRegistrationRequest();
        request.setName("Updated Meal");
        request.setDate(LocalDate.now());

        when(mealRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> mealService.updateMeal(1L, request));
    }

    @Test
    void deleteMeal_ShouldDeleteMealWhenExists() {
        // Arrange
        Meal meal = new Meal();
        meal.setId(1L);

        when(mealRepository.findById(1L)).thenReturn(Optional.of(meal));

        // Act
        mealService.deleteMeal(1L);

        // Assert
        verify(mealRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMeal_ShouldThrowNotFoundException() {
        // Arrange
        when(mealRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> mealService.deleteMeal(1L));
    }
}
