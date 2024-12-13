package be.pxl.activity.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class SleepRegistrationRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Hours of sleep is required")
    private int hours;

    @NotNull(message = "Minutes of sleep is required")
    private int minutes;

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
