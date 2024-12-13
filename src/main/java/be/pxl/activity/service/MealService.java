package be.pxl.activity.service;

import be.pxl.activity.domain.Meal;
import be.pxl.activity.domain.User;
import be.pxl.activity.dto.MealRegistrationRequest;
import be.pxl.activity.dto.MealResponse;
import be.pxl.activity.exception.NotFoundException;
import be.pxl.activity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import be.pxl.activity.repository.MealRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    @Autowired
    public MealService(MealRepository mealRepository, UserRepository userRepository) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
    }

    public void registerMeal(MealRegistrationRequest request, String mail) {

        User user = userRepository.findByEmail(mail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Meal meal = new Meal();
        meal.setName(request.getName());
        meal.setDate(request.getDate());
        meal.setCalories(100);
        meal.setUser(user);

        mealRepository.save(meal);
    }

    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    // meal response is a DTO
    public MealResponse getMealById(Long id) {
        Optional<Meal> mealOptional = mealRepository.findById(id);
        if (mealOptional.isPresent()) {
            Meal meal = mealOptional.get();
            return new MealResponse(
                    meal.getId(),
                    meal.getName(),
                    meal.getDate(),
                    meal.getCalories()
            );
        }else{
            throw new NotFoundException("Meal not found");
        }


    }

    public List<MealResponse> getAllMealsForUser(String mail) {
        User user = userRepository.findByEmail(mail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return mealRepository.findAll().stream()
                .filter(meal -> meal.getUser().getId().equals(user.getId()))
                .map(meal -> new MealResponse(
                        meal.getId(),
                        meal.getName(),
                        meal.getDate(),
                        meal.getCalories()
                ))
                .collect(Collectors.toList());
    }


    public void updateMeal(Long id, MealRegistrationRequest request) {
        Optional<Meal> optionalMeal = mealRepository.findById(id);
        if (optionalMeal.isPresent()) {
            Meal meal = optionalMeal.get();
            meal.setName(request.getName());
            meal.setDate(request.getDate());
            mealRepository.save(meal);
        }else {
            throw new NotFoundException("Meal not found");
        }
    }

    public void deleteMeal(Long id) {
        Optional<Meal> optionalMeal = mealRepository.findById(id);
        if (optionalMeal.isPresent()) {
            mealRepository.deleteById(id);
        }else {
            throw new NotFoundException("Meal not found");
        }
    }
}
