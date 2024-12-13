package be.pxl.activity.dto;

import be.pxl.activity.domain.Distance;

public class SportTotalsResponse {
    private String activity;
    private int totalActivities;
    private double totalCalories;
    private Distance totalDistance;
    private String totalTime;

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    //getter and setters
    public int getTotalActivities() {
        return totalActivities;
    }

    public void setTotalActivities(int totalActivities) {
        this.totalActivities = totalActivities;
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public Distance getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Distance totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }
}
