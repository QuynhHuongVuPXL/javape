package be.pxl.activity.api;

import be.pxl.activity.domain.Activity;
import be.pxl.activity.dto.ActivityRegistrationRequest;
import be.pxl.activity.dto.ActivityStatisticsResponse;
import be.pxl.activity.exception.ForbiddenActionException;
import be.pxl.activity.exception.NotFoundException;
import be.pxl.activity.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {
    private final ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<String> registerActivity(@RequestBody @Valid ActivityRegistrationRequest request, Principal principal) {
        try {
            activityService.registerActivity(request, principal.getName());
            return new ResponseEntity<>("activity created successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping
    public ResponseEntity<List<Activity>> getActivities(Principal principal) {
        try{
            return new ResponseEntity<>(activityService.getAllActivities(principal.getName()), HttpStatus.OK);
        }catch (HttpClientErrorException.Unauthorized e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }
    @DeleteMapping("/{activityId}")
    public ResponseEntity<String> deleteActivity(@PathVariable long activityId , Principal principal) {
        try {
            activityService.deleteActivity(activityId , principal);
            return new ResponseEntity<>("Activity deleted successfully", HttpStatus.OK);
        } catch (ForbiddenActionException e){
            return new ResponseEntity<>("Forbidden", HttpStatus.FORBIDDEN); // dit moet een forbidden exception zijn 403
        }catch (NotFoundException e) {
            return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND); // dit moet een not found 404 excpetion izjn
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<ActivityStatisticsResponse> getStatistics(Principal principal) {
        try{
            return new ResponseEntity<>(activityService.getActivityStatistics(principal.getName()), HttpStatus.OK);
        }catch (HttpClientErrorException.Unauthorized e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{sport}")
    public ResponseEntity<Object> getSport(@PathVariable String sport, Principal principal) {
        try{
            return activityService.getSportTotals(sport, principal.getName());
        }catch (HttpClientErrorException.Unauthorized e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }
}
