package be.pxl.activity.api;

import be.pxl.activity.dto.SleepRegistrationRequest;
import be.pxl.activity.dto.SleepResponse;
import be.pxl.activity.exception.NotFoundException;
import be.pxl.activity.service.SleepService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/sleep")
public class SleepController {

    private final SleepService sleepService;

    @Autowired
    public SleepController(SleepService sleepService) {
        this.sleepService = sleepService;
    }

    @PostMapping
    public ResponseEntity<String> registerSleep(@RequestBody @Valid SleepRegistrationRequest request, Principal principal) {
        try {
            sleepService.registerSleep(request, principal.getName());
            return new ResponseEntity<>("Sleep record created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<SleepResponse>> getAllSleepRecords(Principal principal) {
        try {
            return new ResponseEntity<>(sleepService.getAllSleepRecords(principal.getName()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getSleepById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(sleepService.getSleepById(id), HttpStatus.OK);
        } catch (NotFoundException e){
            return new ResponseEntity<>("Sleep record not found", HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSleep(@PathVariable Long id, @RequestBody @Valid SleepRegistrationRequest request) {
        try {
            sleepService.updateSleep(id, request);
            return new ResponseEntity<>("Sleep record updated successfully", HttpStatus.OK);
        } catch (NotFoundException e){
            return new ResponseEntity<>("Sleep record not found", HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSleep(@PathVariable Long id) {
        try {
            sleepService.deleteSleep(id);
            return new ResponseEntity<>("Sleep record deleted successfully", HttpStatus.OK);
        } catch (NotFoundException e){
            return new ResponseEntity<>("Sleep record not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
