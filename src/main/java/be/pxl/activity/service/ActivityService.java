package be.pxl.activity.service;

import be.pxl.activity.domain.Activity;
import be.pxl.activity.domain.Distance;
import be.pxl.activity.domain.DurationFormatter;
import be.pxl.activity.domain.User;
import be.pxl.activity.dto.ActivityRegistrationRequest;
import be.pxl.activity.dto.ActivityStatisticsResponse;
import be.pxl.activity.dto.SportTotalsResponse;
import be.pxl.activity.exception.ForbiddenActionException;
import be.pxl.activity.exception.NotFoundException;
import be.pxl.activity.repository.ActivityRepository;
import be.pxl.activity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    // Method to register a new activity
    public void registerActivity(ActivityRegistrationRequest request , String email) {
        // Validate the start and end times
        if (request.getStart().isAfter(request.getEnd())) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }

        // Retrieve the User entity based on the email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double caloriesBurned = 0;

        // Create and populate the Activity object
        Activity activity = new Activity();
        activity.setActivity(request.getActivity());

        DurationFormatter durationFormatter = new DurationFormatter();
        Duration duration = Duration.between(request.getStart(), request.getEnd());

        activity.setDuration(durationFormatter.formatDuration(duration));
        activity.setStartTime(request.getStart());
        activity.setEndTime(request.getEnd());
        LocalDate date = request.getStart().toLocalDate(); // date bevat enkel datum van start
        activity.setDate(date);

        if (request.getDistance() != null) {
            activity.getDistance().setValue((float) request.getDistance().getValue());
            activity.getDistance().setUnit(request.getDistance().getUnit());
            // Calculate calories burned
            caloriesBurned = calculateCalories(activity);
        }

        activity.setCaloriesBurned((float) caloriesBurned);
        activity.setUser(user); // Set the full User object

        // Save the activity
        activityRepository.save(activity);
    }


public double calculateCalories(Activity activity) {
    // building the webclient
    WebClient webClient =  WebClient.builder()
            .baseUrl("https://trackapi.nutritionix.com/v2/natural/exercise")
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("x-app-id", "18c0ed15")
            .defaultHeader("x-app-key", "7db55b25dcf1bc4650576d12ab67ec9d")
            .build();
    // Build the JSON payload
    String query = String.format("%s for %s minutes", activity.getActivity(), activity.getDuration());

    // Call the API and parse the response
    try {
        Mono<String> responseMono = webClient.post()
                .bodyValue(String.format("{\"query\": \"%s\"}", query))
                .retrieve()
                .bodyToMono(String.class);

        // Block to get the response synchronously
        String response = responseMono.block();

        // Manually extract calories from the JSON string
        String searchKey = "\"nf_calories\":";
        assert response != null;
        int startIndex = response.indexOf(searchKey);
        if (startIndex != -1) {
            startIndex += searchKey.length();
            int endIndex = response.indexOf(",", startIndex);
            String caloriesString = response.substring(startIndex, endIndex).trim();
            return Double.parseDouble(caloriesString);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return 0.0; // Default value in case of an error
}

    public List<Activity> getAllActivities(String email) {

        return this.activityRepository.findAll().stream()
                .filter(activity -> activity.getUser().getEmail().equals(email))
                .collect(Collectors.toList());
    }

    public void deleteActivity(long activityid , Principal principal){

        // if it doesnt exist throw not found excpetion
        if (!activityRepository.existsById(activityid)) {
            throw new NotFoundException("object not found"); // dit moet een notfoundexcpetion zijn
        }
        Optional<Activity> activityOptional = activityRepository.findById(activityid);
        if (activityOptional.isPresent()) {
            Activity activity = activityOptional.get();
            User user = activity.getUser();

            if (!Objects.equals(user.getEmail(), principal.getName())){
                throw new ForbiddenActionException("User does not own this activity"); // this exception here should be forbidden exception
            }
        }

        activityRepository.deleteById(activityid);
    }

    public ResponseEntity<Object> getSportTotals(String selectedSport, String email) {
        SportTotalsResponse sportTotals = new SportTotalsResponse();

        List<Activity> userActivities = this.activityRepository.findByUserEmailAndSport(email, selectedSport);

        if (userActivities == null || userActivities.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        double totalCalories = 0.0 ;
        int counter = 0;
        Distance totaldistance = new Distance();
        long seconds = 0;

        for (Activity activity : userActivities) {
            totalCalories += activity.getCaloriesBurned();
            totaldistance = totaldistance.add(activity.getDistance());
            seconds += Duration.between(activity.getStartTime(), activity.getEndTime()).toSeconds();
            counter += 1;
        }

        DurationFormatter durationFormatter = new DurationFormatter();

        sportTotals.setActivity(selectedSport);
        sportTotals.setTotalCalories(totalCalories);
        sportTotals.setTotalDistance(totaldistance);
        sportTotals.setTotalTime(durationFormatter.formatDuration(Duration.ofSeconds(seconds)));
        sportTotals.setTotalActivities(counter);

        return ResponseEntity.ok(sportTotals);
    }

    public ActivityStatisticsResponse getActivityStatistics(String email) {
        ActivityStatisticsResponse activityStatistics = new ActivityStatisticsResponse();
        // list of all activities of the logged in person
        List<Activity> userActivities = this.activityRepository.findByUserEmail(email);

        // setting total activities
        activityStatistics.setTotalActivities(userActivities.size());
        // setting total calories
        double totalCalories = userActivities.stream()
                .mapToDouble(Activity::getCaloriesBurned)  // Convert Activity to its calories value
                .sum();  // Sum up the calories
        activityStatistics.setTotalCalories(totalCalories);
        //setting activities counter using map
        Map<String, Integer> activityCountMap = new HashMap<>();

        // Loop through activities and populate the map with counts
        userActivities.forEach(activity -> {
            activityCountMap.put(activity.getActivity(), activityCountMap.getOrDefault(activity.getActivity(), 0) + 1);
        });
        activityStatistics.setActivityCount(activityCountMap);

        // setting heaviest activity i assume here that heaviest activity is most calories burned
        // If there is a activity to be found, set it in the statistics
        Optional<Activity> mostCaloriesBurnedActivity = userActivities.stream()
                .max(Comparator.comparingDouble(Activity::getCaloriesBurned)); // Comparator to find the max

        mostCaloriesBurnedActivity.ifPresent(activityStatistics::setHeaviestActivity);
        // setting the longest activitiy
        // Find the activity with the longest duration using Streams
        Optional<Activity> longestDurationActivity = userActivities.stream()
                .max(Comparator.comparingInt(this::getActivityDurationInSeconds)); // Custom comparator for duration

        // Set the activity with the longest duration in the statistics object
        longestDurationActivity.ifPresent(activityStatistics::setLongestActivity);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // the output always has to be

        // earliest starttime and latest start time
        // Find the activity with the earliest start time
        Optional<Activity> earliestStartActivity = userActivities.stream()
                .min(Comparator.comparing(Activity::getStartTime));

        // Set the activity with the earliest start time in the statistics object
        earliestStartActivity.ifPresent(activity -> activityStatistics.setEarliest(activity.getStartTime().format(formatter)));

        // Find the activity with the latest start time
        Optional<Activity> latestStartActivity = userActivities.stream()
                .max(Comparator.comparing(Activity::getStartTime)); // the field that is getting compared is the starttime

        // Set the activity with the latest start time in the statistics object
        latestStartActivity.ifPresent(activity -> activityStatistics.setLatest(activity.getStartTime().format(formatter)));

        return activityStatistics;
    }

    private int getActivityDurationInSeconds(Activity activity) {
        // Assuming the format is something like "X hours, Y minutes and Z seconds"
        String duration = activity.getDuration();  // Example: "1 hour, 22 minutes and 25 seconds" or "22 minutes and 25 seconds"

        // Remove the "Duration: " part if it exists
        if (duration.startsWith("Duration:")) {
            duration = duration.replace("Duration:", "").trim();
        }

        // Initialize hours, minutes, and seconds
        int hours = 0, minutes = 0, seconds = 0;

        // Handle plural forms by removing "s" from "hours", "minutes", and "seconds"
        duration = duration.replace(" hours", " hour").replace(" minutes", " minute").replace(" seconds", " second");

        // Check if hours are mentioned in the string
        if (duration.contains("hour")) {
            // Split by "hour" to extract hours and the remaining part
            String[] parts = duration.split("hour");
            hours = Integer.parseInt(parts[0].trim()); // Get the number of hours
            duration = parts[1].trim(); // Remaining part after extracting hours
        }

        // Now check if minutes are mentioned
        if (duration.contains("minute")) {
            // Split by "minute" to extract minutes
            String[] parts = duration.split("minute");
            minutes = Integer.parseInt(parts[0].trim()); // Get the number of minutes
            duration = parts[1].trim(); // Remaining part after extracting minutes
        }

        // Now check if seconds are mentioned
        if (duration.contains("second")) {
            // Split by "second" to extract seconds
            String[] parts = duration.split("second");
            seconds = Integer.parseInt(parts[0].trim()); // Get the number of seconds
        }

        // Convert the total duration to seconds and return it
        return hours * 60 * 60 + minutes * 60 + seconds;
    }
}