package com.redditgetter;

import com.redditgetter.Time;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Reddit {
	final static String RDS_HOSTNAME = "reddit-database.cx9mmdexfpd7.us-west-1.rds.amazonaws.com";
	final static String RDS_PORT = "3306";
	final static String RDS_DB_NAME = "reddit-database";
	final static String RDS_USERNAME = "willtucker42";
	final static String RDS_PASSWORD = "Createaccou1090";
    private static final String BASE_URL = "https://www.reddit.com/r/leagueoflegends/rising/";
    private long currentUnixTime = System.currentTimeMillis() / 1000L;
    private Rising rising = new Rising();
    private Time time = new Time();
    private String short_day = "";

    public void getLeagueOfLegendsRising() throws Exception {
    	//System.out.println("In get LeagueOfLegendsRising");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        String weekday_hour_field = getShortDay() + "_" + getHourRange();
        double hourly_average = get_hourly_average(weekday_hour_field);
        rising.r_LeagueOfLegends(retrofit, weekday_hour_field, hourly_average, getMedianTrendingLevel());  

    }
    private static double get_hourly_average(String hour_range)throws Exception{
    	System.out.println("In get get_hourly_average");
		try {
			Connection connection = getConnection();
			PreparedStatement pStatement = connection.prepareStatement("SELECT "+hour_range+" FROM HourlyAverages");
			double hourly_average = 0;
			ResultSet resultSet = pStatement.executeQuery();
			
			while(resultSet.next()) {
				hourly_average = resultSet.getDouble(hour_range);
				System.out.println(hourly_average);
			}
			return hourly_average;
		}catch (Exception e) {
			System.out.println(e);
		}
		return 0;
	}
    private String getShortDay() {
    	//System.out.println("In getShortDay");
        switch (time.getDayOfWeek(currentUnixTime)) {
            case "Monday":
                short_day = "mon";
                break;
            case "Tuesday":
                short_day = "tue";
                break;
            case "Wednesday":
                short_day = "wed";
                break;
            case "Thursday":
                short_day = "thu";
                break;
            case "Friday":
                short_day = "fri";
                break;
            case "Saturday":
                short_day = "sat";
                break;
            case "Sunday":
                short_day = "sun";
                break;
        }
        return short_day;
    }

    private String getHourRange() {
    	//System.out.println("In get getHourRange");
        int hour_posted = time.getHourPosted(currentUnixTime);

        switch (hour_posted) {
            case 0:
                return "0_1";
            case 1:
                return "0_1";
            case 2:
                return "2_4";
            case 3:
                return "2_4";
            case 4:
                return "2_4";
            case 5:
                return "5_7";
            case 6:
                return "5_7";
            case 7:
                return "5_7";
            case 8:
                return "8_10";
            case 9:
                return "8_10";
            case 10:
                return "8_10";
            case 11:
                return "11_13";
            case 12:
                return "11_13";
            case 13:
                return "11_13";
            case 14:
                return "14_16";
            case 15:
                return "14_16";
            case 16:
                return "14_16";
            case 17:
                return "17_19";
            case 18:
                return "17_19";
            case 19:
                return "17_19";
            case 20:
                return "20_23";
            case 21:
                return "20_23";
            case 22:
                return "20_23";
            case 23:
                return "20_23";
            default:
                System.out.println("Not good in Reddit.java getHourRange");
                return "20_23";
        }
    }
    public double getMedianTrendingLevel() throws Exception {
		PreparedStatement pStatement = getConnection()
				.prepareStatement("SELECT trending_level FROM RedditTrendingData WHERE trending=1 LIMIT 500");
		ResultSet resultSet = pStatement.executeQuery();
		double total = 0;
		int amount = 0;
		while (resultSet.next()) {
			//System.out.println(resultSet.getDouble("trending_level"));
			total += resultSet.getDouble("trending_level");
			amount++;
		}
		double median_trending_level = total / (double) amount;
		System.out.println("The median trending level is: " + median_trending_level);
		return median_trending_level;
	}
    private static Connection getConnection() throws Exception {
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			Class.forName(driver);
			String jdbcUrl = "jdbc:mysql://" + RDS_HOSTNAME + ":" + RDS_PORT + "/" + RDS_DB_NAME + "?user=" + RDS_USERNAME + "&password=" + RDS_PASSWORD;
			Connection connection = DriverManager.getConnection(jdbcUrl);
			//System.out.println("Connected");
			return connection;
		} catch (Exception e) {
			System.out.println("ERROR in Reddit getting connection: " + e);
		}

		return null;
	}
}