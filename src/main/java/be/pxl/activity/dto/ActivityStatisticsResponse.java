package be.pxl.activity.dto;

import be.pxl.activity.domain.Activity;
import java.util.Map;

public class ActivityStatisticsResponse {
    private int totalActivities;
    private double totalCalories;
    private Map<String, Integer> activityCount;
    private Activity heaviestActivity;
    private Activity longestActivity;
    private String earliest;
    private String latest;

    public ActivityStatisticsResponse() {
    }
    // Getters and Setters

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

    public Map<String, Integer> getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(Map<String, Integer> activityCount) {
        this.activityCount = activityCount;
    }

    public Activity getHeaviestActivity() {
        return heaviestActivity;
    }

    public void setHeaviestActivity(Activity heaviestActivity) {
        this.heaviestActivity = heaviestActivity;
    }

    public Activity getLongestActivity() {
        return longestActivity;
    }

    public void setLongestActivity(Activity longestActivity) {
        this.longestActivity = longestActivity;
    }

    public String getEarliest() {
        return earliest;
    }

    public void setEarliest(String earliest) {
        this.earliest = earliest;
    }

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }
}


