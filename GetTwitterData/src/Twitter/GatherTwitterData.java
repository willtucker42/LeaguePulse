package Twitter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GatherTwitterData {
	double minute10_percentage, minute11_percentage, minute12_percentage, minute13_percentage, minute14_percentage,
			minute15_percentage, minute16_percentage, minute17_percentage, minute18_percentage, minute19_percentage,
			minute20_percentage;
	int minute10counter, minute11counter, minute12counter, minute13counter, minute14counter, minute15counter,
			minute16counter, minute17counter, minute18counter, minute19counter, minute20counter;

	public void start(Connection connection) throws SQLException {
		System.out.println("Starting...");
		PreparedStatement pStatement = connection
				.prepareStatement("SELECT twitter_handle, minutes_ago_utc, score FROM TwitterData");
		ResultSet rSet = pStatement.executeQuery();

		while (rSet.next()) {
			System.out.print("Adding tweet...");
			int avg_total_engagement = getAverageTotalEngagement(rSet.getString("twitter_handle"), connection);
			
			if(avg_total_engagement !=0) {
				addVariables(rSet.getInt("score"), rSet.getInt("minutes_ago_utc"), avg_total_engagement);
			}else {
				System.out.println("Could not get total engagement average for " + rSet.getString("twitter_handle"));
			}
			
		}
		printResults();
	}
	private void addVariables(int score, int minutes_ago_utc, int user_avg_total_eng) {
		switch (minutes_ago_utc) {
		case 10:
			minute10counter++;
			minute10_percentage += (double) score / (double) user_avg_total_eng;
			System.out.print(minute10_percentage);
			break;
		case 11:
			minute11counter++;
			minute11_percentage += (double) score / (double) user_avg_total_eng;
			System.out.print(minute11_percentage);
			break;
		case 12:
			minute12counter++;
			minute12_percentage += (double) score / (double) user_avg_total_eng;
			System.out.print(minute12_percentage);
			break;
		case 13:
			minute13counter++;
			minute13_percentage += (double) score / (double) user_avg_total_eng;
			System.out.print(minute13_percentage);
			break;
		case 14:
			minute14counter++;
			minute14_percentage += (double) score / (double) user_avg_total_eng;
			System.out.print(minute14_percentage);
			break;
		case 15:
			minute15counter++;
			minute15_percentage += (double) score / (double) user_avg_total_eng;
			System.out.print(minute15_percentage);
			break;
		case 16:
			minute16counter++;
			minute16_percentage += (double) score / (double) user_avg_total_eng;
			System.out.print(minute16_percentage);
			break;
		case 17:
			minute17counter++;
			minute17_percentage += (double) score / (double) user_avg_total_eng;
			System.out.print(minute17_percentage);
			break;
		case 18:
			minute18counter++;
			minute18_percentage += (double) score / (double) user_avg_total_eng;
			System.out.print(minute18_percentage);
			break;
		case 19:
			minute19counter++;
			minute19_percentage += (double) score / (double) user_avg_total_eng;
			System.out.print(minute19_percentage);
			break;
		case 20:
			minute20counter++;
			minute20_percentage += (double) score / (double) user_avg_total_eng;
			System.out.print(minute20_percentage);
			break;
		}
	}
	private void printResults() {
		System.out.println("minute 10 percentage is: " + minute10_percentage + " - minute10 counter is: " + minute10counter);
		System.out.println("minute 11 percentage is: " + minute11_percentage + " - minute11 counter is: " + minute11counter);
		System.out.println("minute 12 percentage is: " + minute12_percentage + " - minute12 counter is: " + minute12counter);
		System.out.println("minute 13 percentage is: " + minute13_percentage + " - minute13 counter is: " + minute13counter);
		System.out.println("minute 14 percentage is: " + minute14_percentage + " - minute14 counter is: " + minute14counter);
		System.out.println("minute 15 percentage is: " + minute15_percentage + " - minute15 counter is: " + minute15counter);
		System.out.println("minute 16 percentage is: " + minute16_percentage + " - minute16 counter is: " + minute16counter);
		System.out.println("minute 17 percentage is: " + minute17_percentage + " - minute17 counter is: " + minute17counter);
		System.out.println("minute 18 percentage is: " + minute18_percentage + " - minute18 counter is: " + minute18counter);
		System.out.println("minute 19 percentage is: " + minute19_percentage + " - minute19 counter is: " + minute19counter);
		System.out.println("minute 20 percentage is: " + minute20_percentage + " - minute20 counter is: " + minute20counter);
		System.out.println("10 Minute percentage: " + minute10_percentage / (double) minute10counter);
		System.out.println("11 Minute percentage: " + minute11_percentage / (double) minute11counter);
		System.out.println("12 Minute percentage: " + minute12_percentage / (double) minute12counter);
		System.out.println("13 Minute percentage: " + minute13_percentage / (double) minute13counter);
		System.out.println("14 Minute percentage: " + minute14_percentage / (double) minute14counter);
		System.out.println("15 Minute percentage: " + minute15_percentage / (double) minute15counter);
		System.out.println("16 Minute percentage: " + minute16_percentage / (double) minute16counter);
		System.out.println("17 Minute percentage: " + minute17_percentage / (double) minute17counter);
		System.out.println("18 Minute percentage: " + minute18_percentage / (double) minute18counter);
		System.out.println("19 Minute percentage: " + minute19_percentage / (double) minute19counter);
		System.out.println("20 Minute percentage: " + minute20_percentage / (double) minute20counter);
	}

	private int getAverageTotalEngagement(String handle, Connection connection) throws SQLException {
		PreparedStatement pStatement = connection.prepareStatement("SELECT avg_total_engagement FROM TwitterUsers WHERE twitter_handle='" + handle + "'");
		ResultSet rSet = pStatement.executeQuery();
		while(rSet.next()) {
			return rSet.getInt("avg_total_engagement");
		}
		return 0;
	}
}
