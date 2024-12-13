package be.pxl.activity.service;

import be.pxl.activity.domain.Sleep;
import be.pxl.activity.domain.User;
import be.pxl.activity.dto.SleepRegistrationRequest;
import be.pxl.activity.dto.SleepResponse;
import be.pxl.activity.exception.NotFoundException;
import be.pxl.activity.repository.SleepRepository;
import be.pxl.activity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SleepService {

    private final SleepRepository sleepRepository;
    private final UserRepository userRepository;

    @Autowired
    public SleepService(SleepRepository sleepRepository, UserRepository userRepository) {
        this.sleepRepository = sleepRepository;
        this.userRepository = userRepository;
    }

    public void registerSleep(SleepRegistrationRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Sleep sleep = new Sleep();
        sleep.setDate(request.getDate());
        sleep.setHours(request.getHours());
        sleep.setMinutes(request.getMinutes());
        sleep.setUser(user);

        sleepRepository.save(sleep);
    }

    public List<SleepResponse> getAllSleepRecords(String userEmail) {
        return sleepRepository.findByUserEmail(userEmail).stream()
                .map(sleep -> new SleepResponse(
                        sleep.getId(),
                        sleep.getDate(),
                        sleep.getHours(),
                        sleep.getMinutes()
                ))
                .collect(Collectors.toList());
    }

    public SleepResponse getSleepById(Long id) {
        Optional<Sleep> sleep = sleepRepository.findById(id);
        if (sleep.isPresent()) {
            Sleep selectedSleep = sleep.get();

            return new SleepResponse(
                    selectedSleep.getId(),
                    selectedSleep.getDate(),
                    selectedSleep.getHours(),
                    selectedSleep.getMinutes()
            );
        } else {
            throw new NotFoundException("Sleep not found");
        }
    }

    public void updateSleep(Long id, SleepRegistrationRequest request) {
        Sleep sleep = sleepRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sleep record not found"));

        sleep.setDate(request.getDate());
        sleep.setHours(request.getHours());
        sleep.setMinutes(request.getMinutes());

        sleepRepository.save(sleep);
    }

    public void deleteSleep(Long id) {
        Optional<Sleep> sleep = sleepRepository.findById(id);
        if (sleep.isPresent()) {
            sleepRepository.deleteById(id);
        } else {
            throw new NotFoundException("Sleep not found");
        }

    }
}
