package be.pxl.activity.domain;

import java.time.Duration;

public class DurationFormatter {

    // Helper method to handle pluralization
    private String pluralize(long value, String unit) {
        return String.format("%d %s%s", value, unit, value != 1 ? "s" : "");
    }

    public String formatDuration(Duration duration) {
        // Get the total hours, minutes, and seconds
        long hours = duration.toHours();  // Total hours
        long minutes = duration.toMinutes() % 60;  // Minutes excluding hours
        long seconds = duration.getSeconds() % 60;  // Seconds excluding minutes

        // Build the formatted string based on the available time units
        StringBuilder result = new StringBuilder();

        // Append hours if greater than 0
        if (hours > 0) {
            result.append(pluralize(hours, "hour"));
        }

        // Append minutes if greater than 0
        if (minutes > 0) {
            if (!result.isEmpty()) result.append(" ");
            result.append(pluralize(minutes, "minute"));
        }

        // Append seconds if greater than 0
        if (seconds > 0) {
            if (!result.isEmpty()) result.append(" ");
            result.append(pluralize(seconds, "second"));
        }

        // If duration is 0 (i.e., no hours, minutes, or seconds), return "0 seconds"
        if (result.isEmpty()) {
            return "0 seconds";
        }

        return result.toString();
    }
}
