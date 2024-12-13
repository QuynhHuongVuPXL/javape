package be.pxl.activity.dto;

import java.time.LocalDate;

public class SleepResponse {

    private Long id;
    private LocalDate date;
    private int hours;
    private int minutes;

    public SleepResponse(Long id, LocalDate date, int hours, int minutes) {
        this.id = id;
        this.date = date;
        this.hours = hours;
        this.minutes = minutes;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
