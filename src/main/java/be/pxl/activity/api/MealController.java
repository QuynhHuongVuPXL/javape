package be.pxl.activity.api;

import be.pxl.activity.dto.MealRegistrationRequest;
import be.pxl.activity.dto.MealResponse;
import be.pxl.activity.exception.NotFoundException;
import be.pxl.activity.service.MealService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/meals")
public class MealController {

    private final MealService mealService;

    @Autowired
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public ResponseEntity<String> registerMeal(@RequestBody @Valid MealRegistrationRequest request, Principal principal) {
        try {
            mealService.registerMeal(request, principal.getName());
            return new ResponseEntity<>("Meal created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMealById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(mealService.getMealById(id), HttpStatus.OK); // uses the dto
        } catch (NotFoundException e){
            return new ResponseEntity<>("Meal not Found",HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>("An error occurred while fetching the meal", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<MealResponse>> getMeals(Principal principal) {
        try {
            return new ResponseEntity<>(mealService.getAllMealsForUser(principal.getName()), HttpStatus.OK);
        } catch (HttpClientErrorException.Unauthorized e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateMeal(@PathVariable Long id, @RequestBody @Valid MealRegistrationRequest request) {
        try {
            mealService.updateMeal(id, request);
            return new ResponseEntity<>("Meal updated successfully", HttpStatus.OK);
        } catch (NotFoundException e){
            return new ResponseEntity<>("Meal not Found", HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMeal(@PathVariable Long id) {
        try {
            mealService.deleteMeal(id);
            return new ResponseEntity<>("Meal deleted successfully", HttpStatus.OK);
        }catch (NotFoundException e){
            return new ResponseEntity<>("Meal not Found", HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

