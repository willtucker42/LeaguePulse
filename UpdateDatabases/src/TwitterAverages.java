import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

public class TwitterAverages {
	final static String RDS_HOSTNAME = "reddit-database.cx9mmdexfpd7.us-west-1.rds.amazonaws.com";
	final static String RDS_PORT = "3306";
	final static String RDS_DB_NAME = "reddit-database";
	final static String RDS_USERNAME = "willtucker42";
	final static String RDS_PASSWORD = "xxxxxxx";

	public void startUpdate(Connection connection, twitter4j.Twitter twitter) throws Exception {
		ArrayList<String> twitter_handles = getTwitterHandles(connection);
		for (String twitter_handle : twitter_handles) {
			updateTwitterUserAverages(twitter_handle, connection, twitter);
		}
	}

	private ArrayList<String> getTwitterHandles(Connection connection) throws Exception {
		ArrayList<String> twitter_handles = new ArrayList<>();
		PreparedStatement pStatement = connection.prepareStatement("SELECT twitter_handle FROM TwitterUsers");
		ResultSet resultSet = pStatement.executeQuery();

		while (resultSet.next()) {
			twitter_handles.add(resultSet.getString("twitter_handle"));
		}
		return twitter_handles;

	}

	public void updateTwitterUserAverages(String handle, Connection connection, twitter4j.Twitter twitter)
			throws SQLException, Exception {
		int average_total_engagement = getAverageTotalEngagement(handle, connection, twitter);
		int average_at_20 = (int) Math.round(average_total_engagement * .06);

		final String insert_statement = "UPDATE TwitterUsers SET avg_total_engagement=" + average_total_engagement
				+ ", avg_at_20=" + average_at_20 + " WHERE twitter_handle='" + handle + "'";
		
		PreparedStatement pStatement = connection.prepareStatement(insert_statement);
		pStatement.executeUpdate();

		System.out.println("Finished updating " + handle + " new Average is " + average_total_engagement);
	}

	private int getAverageTotalEngagement(String handle, Connection conn, twitter4j.Twitter twitter)
			throws TwitterException {
		List<Status> statuses;
		Paging p = new Paging();
		p.count(75);
		statuses = twitter.getUserTimeline(handle, p);
		int number_of_tweets = 0;
		int tweet_engagement_sum = 0;
		double average_total_engagement;
		if (statuses != null) {
			for (Status status : statuses) {
				String text = status.getText();
				if (!Arrays.toString(status.getURLEntities()).contains("expandedURL=")) {
					if (status.getInReplyToScreenName() == null && !text.contains("RT @")) {
						System.out.println("Not a reply, Adding..... " + text);
						tweet_engagement_sum += status.getFavoriteCount() + status.getRetweetCount();
						number_of_tweets++;
					} else {
						// System.out.println("This tweet is a reply");
					}
				} else {
					// System.out.println("This tweet from " + handle + " contains a link");
					// System.out.println("Found one lol");
				}
			}
		}
		if (number_of_tweets > 0) {
			average_total_engagement = (double) tweet_engagement_sum / (double) number_of_tweets;
		} else {
			System.out.println("ERROR ADDING " + handle);
			average_total_engagement = 0;
		}

		return (int) Math.round(average_total_engagement);
	}

	public Connection getConnection() throws Exception {

		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			Class.forName(driver);
			String jdbcUrl = "jdbc:mysql://" + RDS_HOSTNAME + ":" + RDS_PORT + "/" + RDS_DB_NAME + "?user="
					+ RDS_USERNAME + "&password=" + RDS_PASSWORD;
			Connection connection = DriverManager.getConnection(jdbcUrl);
			// System.out.println("Connected");
			return connection;
		} catch (Exception e) {
			System.out.println("ERROR: " + e);
		}

		return null;
	}

}
