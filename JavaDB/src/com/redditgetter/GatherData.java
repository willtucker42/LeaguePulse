package com.redditgetter;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class GatherData {

    private static final String lol_sub_id = "tnaFQHzOAB";

    void addRedditHourlyAverages(String subreddit, String day_of_week, double avg_0_1,
                                 double avg_2_4, double avg_5_7, double avg_8_10,
                                 double avg_11_13, double avg_14_16, double avg_17_19,
                                 double avg_20_23) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("HourlyAverages");
        query.whereEqualTo("objectId", lol_sub_id);

        List<ParseObject> queryList = query.find();
        if (queryList.size() == 1) {
            for (ParseObject parseObject : queryList) {
                putHours(subreddit, day_of_week, parseObject, avg_0_1, avg_2_4, avg_5_7, avg_8_10,
                        avg_11_13, avg_14_16, avg_17_19, avg_20_23);
            }
        } else {
            System.out.println("We found more than 1 parse object with the id of " + lol_sub_id);
        }

    }

    private void putHours(String subreddit, String day_of_week, ParseObject parseObject,
                          double avg_0_1, double avg_2_4, double avg_5_7, double avg_8_10,
                          double avg_11_13, double avg_14_16, double avg_17_19, double avg_20_23)
            throws ParseException {
        String day = createHourlyFieldString(day_of_week);
        parseObject.put("subreddit", subreddit);
        double parse_0_1 = (parseObject.getDouble(day + "_0_1"));
        double parse_2_4 = (parseObject.getDouble(day + "_2_4"));
        double parse_5_7 = (parseObject.getDouble(day + "_5_7"));
        double parse_8_10 = (parseObject.getDouble(day + "_8_10"));
        double parse_11_13 = (parseObject.getDouble(day + "_11_13"));
        double parse_14_16 = (parseObject.getDouble(day + "_14_16"));
        double parse_17_19 = (parseObject.getDouble(day + "_17_19"));
        double parse_20_23 = (parseObject.getDouble(day + "_20_23"));

        if (parse_0_1 != 0) {
            parseObject.put(day + "_0_1", (parse_0_1 + avg_0_1) / (double) 2);
        } else {
            parseObject.put(day + "_0_1", avg_0_1);
        }
        if (parse_2_4 != 0) {
            parseObject.put(day + "_2_4", (parse_2_4 + avg_2_4) / (double) 2);
        } else {
            parseObject.put(day + "_2_4", avg_2_4);
        }
        if (parse_5_7 != 0) {
            parseObject.put(day + "_5_7", (parse_5_7 + avg_5_7) / (double) 2);
        } else {
            parseObject.put(day + "_5_7", avg_5_7);
        }
        if (parse_8_10 != 0) {
            parseObject.put(day + "_8_10", (parse_8_10 + avg_8_10) / (double) 2);
        } else {
            parseObject.put(day + "_8_10", avg_8_10);
        }
        if (parse_11_13 != 0) {
            parseObject.put(day + "_11_13", (parse_11_13 + avg_11_13) / (double) 2);
        } else {
            parseObject.put(day + "_11_13", avg_11_13);
        }
        if (parse_14_16 != 0) {
            parseObject.put(day + "_14_16", (parse_14_16 + avg_14_16) / (double) 2);
        } else {
            parseObject.put(day + "_14_16", avg_14_16);
        }
        if (parse_17_19 != 0) {
            parseObject.put(day + "_17_19", (parse_17_19 + avg_17_19) / (double) 2);
        } else {
            parseObject.put(day + "_17_19", avg_17_19);
        }
        if (parse_20_23 != 0) {
            parseObject.put(day + "_20_23", (parse_20_23 + avg_20_23) / (double) 2);
        } else {
            parseObject.put(day + "_20_23", avg_20_23);
        }
        parseObject.save();
    }


    private String createHourlyFieldString(String day_of_week) {
        String shortened_day_of_week = "";
        switch (day_of_week) {
            case "Monday":
                shortened_day_of_week = "mon";
                break;
            case "Tuesday":
                shortened_day_of_week = "tue";
                break;
            case "Wednesday":
                shortened_day_of_week = "wed";
                break;
            case "Thursday":
                shortened_day_of_week = "thu";
                break;
            case "Friday":
                shortened_day_of_week = "fri";
                break;
            case "Saturday":
                shortened_day_of_week = "sat";
                break;
            case "Sunday":
                shortened_day_of_week = "sun";
                break;
        }

        return shortened_day_of_week;
    }

}