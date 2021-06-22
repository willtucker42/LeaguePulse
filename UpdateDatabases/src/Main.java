import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Main {
	public static void main(String[] args) throws SQLException, Exception {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("Ncv9BXh9l6CncEcvK5DVHlpMN")
				.setOAuthConsumerSecret("v8wFMrKkPSVQ8CZ691qqm1NupV824dkuqPCdbgKkqq1ipVyPVw")
				.setOAuthAccessToken("846952608094978048-eYvaZTiiz0VciCVcEDtFG4PaWgYMBhZ")
				.setOAuthAccessTokenSecret("3AedXLphMHPjmJkmS5GF2dxbtmEwWEX4gyfI3O2QOhJMK");
		TwitterAverages twitterAverages = new TwitterAverages();
		Connection connection = twitterAverages.getConnection();
		TwitterFactory twitterFactory = new TwitterFactory(cb.build());
		twitter4j.Twitter twitter = twitterFactory.getInstance();
		twitterAverages.startUpdate(connection, twitter);
		/*RedditHourlyAverages averages = new RedditHourlyAverages();
		String insert_statement = "UPDATE HourlyAverages SET (sun_20_23) VALUES (?) WHERE id='OqjWscr0p5'";
		PreparedStatement pStatement = connection.prepareStatement(insert_statement);
		pStatement.setBigDecimal(1, BigDecimal.valueOf(0.05525576877866964));
		pStatement.executeUpdate();
		System.out.println("done");*/
		/*System.out.println("Averages for monday: " + averages.startUpdate(connection, "Monday"));
		System.out.println("Averages for monday: " + averages.startUpdate(connection, "Tuesday"));
		System.out.println("Averages for monday: " + averages.startUpdate(connection, "Wednesday"));
		System.out.println("Averages for monday: " + averages.startUpdate(connection, "Thursday"));
		System.out.println("Averages for monday: " + averages.startUpdate(connection, "Friday"));
		System.out.println("Averages for monday: " + averages.startUpdate(connection, "Saturday"));
		System.out.println("Averages for monday: " + averages.startUpdate(connection, "Sunday"));*/
	}
}
