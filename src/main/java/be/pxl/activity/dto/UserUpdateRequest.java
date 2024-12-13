package be.pxl.activity.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class UserUpdateRequest {

    @NotNull(message = "Weight is required")
    private Double weight;

    @NotNull(message = "Height is required")
    private Double height;

    @NotNull(message = "Birthdate is required")
    private LocalDate birthdate;

    public UserUpdateRequest(Double weight, Double height, LocalDate birthdate) {
        this.weight = weight;
        this.height = height;
        this.birthdate = birthdate;
    }

    // Getters and Setters
    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
}
