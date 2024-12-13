package be.pxl.activity.dto;

import java.time.LocalDate;

public class MealResponse {
    private Long id;
    private String name;
    private LocalDate date;
    private int calories;

    public MealResponse(Long id, String name, LocalDate date, int calories) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.calories = calories;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}
