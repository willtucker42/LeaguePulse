package com.redditgetter;


import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RedditDataServer {
	final static String RDS_HOSTNAME = "reddit-database.cx9mmdexfpd7.us-west-1.rds.amazonaws.com";
	final static String RDS_PORT = "3306";
	final static String RDS_DB_NAME = "reddit-database";
	final static String RDS_USERNAME = "willtucker42";
	final static String RDS_PASSWORD = "Createaccou1090";
    private double avg_0_1 = 0, avg_2_4 = 0, avg_5_7 = 0, avg_8_10 = 0, avg_11_13 = 0, avg_14_16 = 0,
            avg_17_19 = 0, avg_20_23 = 0;

    private int count_11_13 = 0, count_14_16 = 0, count_17_19 = 0, count_20_23 = 0, count_0_1 = 0,
            count_2_4 = 0, count_5_7 = 0, count_8_10 = 0;
    private Time time = new Time();

   
    public void addRedditInfoToSql(String id, String title, String author, int score,
            String subreddit, int created_utc, int current_utc,
            int minutes_ago_utc, double score_per_minute, String url,
            String permalink, boolean trending, String self_text,
            double trending_level, Connection connection, String alert_identifier) throws ParseException {
    	System.out.println("Adding to server: " + id);
    	if(trending) {
    		addRedditInfoToTrendingSql(id, title, author, score, subreddit, created_utc, current_utc,
    				minutes_ago_utc, score_per_minute, url, permalink, trending, self_text, trending_level, connection);
    		
    		addToTrendingRedditTwitter(id, title, author, score, subreddit, created_utc, current_utc, minutes_ago_utc,
    				score_per_minute, url, permalink, trending, self_text, trending_level, connection, alert_identifier);
    	}
    	BigDecimal score_per_minute_bd = BigDecimal.valueOf(score_per_minute);
		BigDecimal trending_level_bd = BigDecimal.valueOf(trending_level);
    	final String insert_statement = "INSERT INTO RedditData (id, title, author, score, score_per_minute,"
				+ " minutes_ago_utc, trending_level, hour_of_day, day_of_week, url, trending, created_utc, current_utc, subreddit, permalink, self_text)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
		PreparedStatement pStatement = connection.prepareStatement(insert_statement);
		pStatement.setString(1, id);//id
		pStatement.setString(2, title);//title
		pStatement.setString(3, author);//author
		pStatement.setInt(4, score);//score
		pStatement.setBigDecimal(5, score_per_minute_bd);//score_per_minute
		pStatement.setInt(6, minutes_ago_utc);//minutes_ago_utc
		pStatement.setBigDecimal(7, trending_level_bd);//trending_level
		pStatement.setInt(8, time.getHourPosted(created_utc));//hour_of_day
		pStatement.setString(9, time.getDayOfWeek(created_utc));//day_of_week
		pStatement.setString(10, url);//url
		pStatement.setBoolean(11, trending);//trending
		pStatement.setInt(12, created_utc);//created_utc
		pStatement.setInt(13, current_utc);//current_utc
		pStatement.setString(14, subreddit);//subreddit
		pStatement.setString(15, permalink);//permalink
		pStatement.setString(16, self_text);
		pStatement.executeUpdate();
		}catch (Exception e) {
			System.out.println("EDRROR ADDING DATA TO REDDITDATA");
			System.out.println(e);
		}finally {
			//System.out.println("Finished adding data");
		}
    }
    public void addRedditInfoToTrendingSql(String id, String title, String author, int score,
            String subreddit, int created_utc, int current_utc,
            int minutes_ago_utc, double score_per_minute, String url,
            String permalink, boolean trending, String self_text,
            double trending_level, Connection connection) throws ParseException {
    	System.out.println("Adding to trending server... Trending level: " + trending_level + " score_per_minute is: " + score_per_minute + " id is: " + id);
    	BigDecimal score_per_minute_bd = BigDecimal.valueOf(score_per_minute);
		BigDecimal trending_level_bd = BigDecimal.valueOf(trending_level);
    	final String insert_statement = "INSERT INTO RedditTrendingData (id, title, author, score, score_per_minute,"
				+ " minutes_ago_utc, trending_level, hour_of_day, day_of_week, url, trending, created_utc, current_utc, subreddit, permalink, self_text)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
		PreparedStatement pStatement = connection.prepareStatement(insert_statement);
		pStatement.setString(1, id);//id
		pStatement.setString(2, title);//title
		pStatement.setString(3, author);//author
		pStatement.setInt(4, score);//score
		pStatement.setBigDecimal(5, score_per_minute_bd);//score_per_minute
		pStatement.setInt(6, minutes_ago_utc);//minutes_ago_utc
		pStatement.setBigDecimal(7, trending_level_bd);//trending_level
		pStatement.setInt(8, time.getHourPosted(created_utc));//hour_of_day
		pStatement.setString(9, time.getDayOfWeek(created_utc));//day_of_week
		pStatement.setString(10, url);//url
		pStatement.setBoolean(11, trending);//trending
		pStatement.setInt(12, created_utc);//created_utc
		pStatement.setInt(13, current_utc);//current_utc
		pStatement.setString(14, subreddit);//subreddit
		pStatement.setString(15, permalink);//permalink
		pStatement.setString(16, self_text);
		pStatement.executeUpdate();
		}catch (Exception e) {
			System.out.println("ERROR ADDING DATA TO REDDITTRENDINGDATA");
			System.out.println(e);
		}finally {
			//System.out.println("Finished adding data");
		}
    	
    }
    private void addToTrendingRedditTwitter(String id, String title, String author, int score,
            String subreddit, int created_utc, int current_utc,
            int minutes_ago_utc, double score_per_minute, String url,
            String permalink, boolean trending, String self_text,
            double trending_level, Connection connection, String alert_identifier) {
    	System.out.println("Adding post by " + author + " to trendingreddittwitter server...");
    	BigDecimal trending_level_bd = BigDecimal.valueOf(trending_level);
    	BigDecimal score_per_minute_bd = BigDecimal.valueOf(score_per_minute);
    	final String insert_statement = "INSERT INTO TrendingRedditTwitter (twitter_id, reddit_id, twitter_handle, twitter_name, created_utc,"
				+ " current_utc, day_of_week, hour_of_day, reddit_author, minutes_ago_utc, permalink, score, score_per_minute, self_text, subreddit,"
				+ " title, trending, trending_level, url, twitter_media_url)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    	try {
			PreparedStatement pStatement = connection.prepareStatement(insert_statement);
			pStatement.setLong(1, 0);// twitter id
			pStatement.setString(2, id);// reddit id
			pStatement.setString(3, "");
			pStatement.setString(4, "");
			pStatement.setInt(5, created_utc);
			pStatement.setInt(6, current_utc);
			pStatement.setString(7, time.getDayOfWeek(created_utc));// day of week
			pStatement.setInt(8, time.getHourPosted(created_utc));// hour of day
			pStatement.setString(9, author);// reddit author
			pStatement.setInt(10, minutes_ago_utc);
			pStatement.setString(11, permalink);
			pStatement.setInt(12, score);
			pStatement.setBigDecimal(13, score_per_minute_bd);// score per minute
			pStatement.setString(14, self_text);
			pStatement.setString(15, subreddit);// subreddit
			pStatement.setString(16, title);// title
			pStatement.setBoolean(17, trending);
			pStatement.setBigDecimal(18, trending_level_bd);
			pStatement.setString(19, url);
			pStatement.setString(20, "");//twittermediaurl
			pStatement.executeUpdate();
			FirebaseSendMessage firebaseSendMessage = new FirebaseSendMessage();
			firebaseSendMessage.sendNotification("League Pulse Trending", title, alert_identifier);
		} catch (Exception e) {
			System.out.println(e.toString());
			System.out.println("ERROR ADDING DATA TO TRENDINGREDDITTWITTER");
		}finally {
		}
    }


    public ArrayList<ArrayList<String>> getRedditLists() throws ParseException {
        ArrayList<ArrayList<String>> listOfRedditPostLists = new ArrayList<>();
        DecimalFormat d_format = new DecimalFormat("#.#");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.US);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TrendingRedditPost");
        ArrayList<String> post_titles = new ArrayList<>();
        ArrayList<String> self_texts = new ArrayList<>();
        ArrayList<String> authors = new ArrayList<>();
        ArrayList<String> clickable_link = new ArrayList<>();
        ArrayList<String> link_to_reddit = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> trending_levels = new ArrayList<>();
        query.setLimit(20);
        query.orderByDescending("created_utc");
        //query.whereEqualTo("trending", true);
        List<ParseObject> query_list = query.find();
        for (ParseObject reddit_post : query_list) {
            String self_text = reddit_post.getString("self_text");
            Date date = new Date(reddit_post.getLong("created_utc") * 1000L);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America"));
            trending_levels.add(String.valueOf(d_format.format(reddit_post.getDouble("trending_level"))));
            dates.add(simpleDateFormat.format(date));
            post_titles.add(reddit_post.getString("title"));
            if (self_text == null || self_text.equals("")) {
                self_texts.add(reddit_post.getString("url")); //adds url of link to article/video if there is no selftext
                clickable_link.add("yes");
            } else {
                self_texts.add(reddit_post.getString("self_text"));
                clickable_link.add("no");
            }
            authors.add("/u/" + reddit_post.getString("author"));
            link_to_reddit.add(reddit_post.getString("permalink"));
        }
        listOfRedditPostLists.add(post_titles);
        listOfRedditPostLists.add(self_texts);
        listOfRedditPostLists.add(authors);
        listOfRedditPostLists.add(clickable_link);
        listOfRedditPostLists.add(link_to_reddit);
        listOfRedditPostLists.add(dates);
        listOfRedditPostLists.add(trending_levels);
        return listOfRedditPostLists;
    }

    public String getDailyAverages(String day_of_week) throws ParseException {
        GatherData gatherData = new GatherData();
        ParseQuery<ParseObject> dailyAverages = ParseQuery.getQuery("RedditDataTest");
        dailyAverages.whereEqualTo("day_of_week", day_of_week);
        System.out.println("Day of week being tested is: " + day_of_week);
        dailyAverages.setLimit(5000);
        List<ParseObject> queryList = dailyAverages.find();

        if (queryList.size() > 0) {
            for (ParseObject redditPost : queryList) {
                double score_per_minute = redditPost.getDouble("score_per_minute");
                int hour_of_day = redditPost.getInt("hour_of_day");
                // System.out.println("Hour of day for this post is: " +
                //  redditPost.getInt("hour_of_day"));
                getHourlyAverages(hour_of_day, score_per_minute);
            }
        } else {
            return "Could not get a list for " + day_of_week;
        }
        createAverages();
        gatherData.addRedditHourlyAverages("leagueoflegends", day_of_week, avg_0_1, avg_2_4,
                avg_5_7, avg_8_10, avg_11_13, avg_14_16, avg_17_19, avg_20_23);

        return "Hourly Averages for " + day_of_week + ": \n" +
                "11-13: " + avg_11_13 + "\n" +
                "14-16: " + avg_14_16 + "\n" +
                "17-19: " + avg_17_19 + "\n" +
                "20-23: " + avg_20_23 + "\n" +
                "0-1: " + avg_0_1 + "\n" +
                "2-4: " + avg_2_4 + "\n" +
                "5-7: " + avg_5_7 + "\n" +
                "8-10: " + avg_8_10;
    }

    private void getHourlyAverages(int hour_of_day, double score_per_minute) {
        if (hour_of_day == 11 || hour_of_day == 12 || hour_of_day == 13) {
            // System.out.println("We found a 11 12 or 13.. spm: " + score_per_minute);
            avg_11_13 += score_per_minute;
            count_11_13++;
        } else if (hour_of_day == 14 || hour_of_day == 15 || hour_of_day == 16) {
            //  System.out.println("We found a 14 15 or 16.. spm: " + score_per_minute);
            avg_14_16 += score_per_minute;
            count_14_16++;
        } else if (hour_of_day == 17 || hour_of_day == 18 || hour_of_day == 19) {
            //  System.out.println("We found a 17 18 or 19.. spm: " + score_per_minute);
            avg_17_19 += score_per_minute;
            count_17_19++;
        } else if (hour_of_day == 20 || hour_of_day == 21 || hour_of_day == 22 || hour_of_day == 23) {
            //   System.out.println("We found a 20 21 22 or 23.. spm: " + score_per_minute);
            avg_20_23 += score_per_minute;
            count_20_23++;
        } else if (hour_of_day == 0 || hour_of_day == 1) {
            //  System.out.println("We found a 0 or 1.. spm: " + score_per_minute);
            avg_0_1 += score_per_minute;
            count_0_1++;
        } else if (hour_of_day == 2 || hour_of_day == 3 || hour_of_day == 4) {
            //  System.out.println("We found a 2 3 or 4.. spm: " + score_per_minute);
            avg_2_4 += score_per_minute;
            count_2_4++;
        } else if (hour_of_day == 5 || hour_of_day == 6 || hour_of_day == 7) {
            //  System.out.println("We found a 5 6 or 7.. spm: " + score_per_minute);
            avg_5_7 += score_per_minute;
            count_5_7++;
        } else if (hour_of_day == 8 || hour_of_day == 9 || hour_of_day == 10) {
            //  System.out.println("We found a 8 9 or 10.. spm: " + score_per_minute);
            avg_8_10 += score_per_minute;
            count_8_10++;
        } else {
            System.out.println("getHourlyAverage: There was an error getting hour_of_day");
        }
    }

    private void createAverages() {
        avg_11_13 = avg_11_13 / (double) count_11_13;
        avg_14_16 = avg_14_16 / (double) count_14_16;
        avg_17_19 = avg_17_19 / (double) count_17_19;
        avg_20_23 = avg_20_23 / (double) count_20_23;
        avg_0_1 = avg_0_1 / (double) count_0_1;
        avg_2_4 = avg_2_4 / (double) count_2_4;
        avg_5_7 = avg_5_7 / (double) count_5_7;
        avg_8_10 = avg_8_10 / (double) count_8_10;
    }
}
