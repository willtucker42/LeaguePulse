package com.redditgetter;

import java.util.Calendar;
import java.util.TimeZone;

public class Time {
    private Calendar calendar = Calendar.getInstance();
    public int getHourPosted(long created_utc) {

        calendar.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        calendar.setTimeInMillis(created_utc * 1000);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    public String getDayOfWeek(long created_utc) {
        String day_of_week = null;
        calendar.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        calendar.setTimeInMillis(created_utc * 1000);
       // System.out.println("Day of week: " + calendar.get(Calendar.DAY_OF_WEEK));
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                day_of_week = "Sunday";
                break;
            case 2:
                day_of_week = "Monday";
                break;
            case 3:
                day_of_week = "Tuesday";
                break;
            case 4:
                day_of_week = "Wednesday";
                break;
            case 5:
                day_of_week = "Thursday";
                break;
            case 6:
                day_of_week = "Friday";
                break;
            case 7:
                day_of_week = "Saturday";
                break;
        }
        //System.out.println("Day of week set to: " + day_of_week);
        return day_of_week;
    }
}
