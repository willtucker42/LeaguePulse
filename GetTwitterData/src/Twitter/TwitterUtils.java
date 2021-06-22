package Twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.InputMap;

import firebase.FirebaseSendMessage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

public class TwitterUtils {
	int current_engagement_score;
	double compare_score;
	final static String RDS_HOSTNAME = "reddit-database.cx9mmdexfpd7.us-west-1.rds.amazonaws.com";
	final static String RDS_PORT = "3306";
	final static String RDS_DB_NAME = "reddit-database";
	final static String RDS_USERNAME = "willtucker42";
	final static String RDS_PASSWORD = "Createaccou1090";

	public void checkUserForTrendingTweets(String username, Connection connection, twitter4j.Twitter twitter,
			List<Status> statuses, Paging p, Date date, double median_trending_level) {
		try {
			System.out.println("Checking " + username + " for trending tweets... ");
			String twitter_handle = username, twitter_name = getTwitterName(connection, username),
					twitter_media_url = null, permalink = null, self_text = null, profile_pic_url = null,
					media_type = null;
			FirebaseSendMessage fMessage = new FirebaseSendMessage();
			int created_utc, current_utc, minutes_ago_utc;
			boolean trending = false;
			double trending_level = 0;
			long twitter_id = 0;
			statuses = null;
			if (connection != null) {
				// System.out.println("Connected...");
				statuses = twitter.getUserTimeline(username, p);
				if (statuses != null) {
					// System.out.println("Statuses is not null...");
					for (Status status : statuses) {
						// System.out.println("Checking a status for... " + username);
						created_utc = (int) (status.getCreatedAt().getTime() / 1000);
						current_utc = (int) (date.getTime() / 1000);
						int utc_ago = (int) (current_utc - created_utc);
						minutes_ago_utc = (int) utc_ago / 60;
						twitter_id = status.getId();
						if (!isOnServer(twitter_id, connection)) {
							if (minutes_ago_utc >= 10 && minutes_ago_utc <= 20) {
								// System.out.println("Status meets time requirements...");

								self_text = status.getText();
								if (status.getInReplyToScreenName() == null && !self_text.contains("RT @") && !otherGamesMentioned(self_text)) {
									// System.out.println("Status is not a retweet or a reply...");
									profile_pic_url = status.getUser().get400x400ProfileImageURL();
									permalink = "https://twitter.com/" + username + "/status/" + twitter_id;
									current_engagement_score = status.getFavoriteCount() + status.getRetweetCount();
									if (isTrending(username, minutes_ago_utc, current_engagement_score, connection)) {
										System.out.println(" TRENDING POST ----------------------------> ");
										addToTrendingTweetCount(twitter_handle, connection);
										addToTweetCount(twitter_handle, connection);
										trending = true;
										trending_level = (double) current_engagement_score / compare_score;
										twitter_id = status.getId();

										MediaEntity[] media = status.getMediaEntities();
										if (media.length != 0) {
											for (MediaEntity m : media) {
												System.out.print(
														"Inside mediaentity for loop... This should only happen once...");
												MediaEntity.Variant[] variant = m.getVideoVariants();
												if (m.getType().equals("video")) {
													System.out.print("Video identified.");
													media_type = "video";
													twitter_media_url = variant[0].getUrl();
													break;
												} else if (m.getType().equals("photo")) {
													System.out.print("Photo Identified");
													media_type = "photo";
													twitter_media_url = media[0].getMediaURL();
													break;
												} else if (m.getType().equals("animated_gif")) {
													System.out.print("Animated j");
													media_type = "animated_gif";
													twitter_media_url = variant[0].getUrl();
													break;
												} else {
													media_type = "";
													twitter_media_url = "";
													break;
												}
											}
										} else {
											media_type = "";
											twitter_media_url = "";
										}
										String alert_identifier = null;
										if (trending_level >= getMedianTrendingLevel(connection)) {
											alert_identifier = "trending:top type:twitter region:"
													+ getRegionFromTwitterUser(twitter_handle, connection);
										} else {
											alert_identifier = "trending:all type:twitter region:"
													+ getRegionFromTwitterUser(twitter_handle, connection);
										}
										fMessage.sendNotification(twitter_name + " is trending on Twitter: ", self_text,
												alert_identifier);
										addToSql(twitter_id, twitter_handle, twitter_name, created_utc, current_utc,
												minutes_ago_utc, permalink, current_engagement_score, self_text,
												trending, trending_level, twitter_media_url, connection,
												profile_pic_url, media_type, calcTrendingThreshold(twitter_handle, minutes_ago_utc, current_utc, connection));
										addToTrendingSql(twitter_id, twitter_handle, twitter_name, created_utc,
												current_utc, minutes_ago_utc, permalink, current_engagement_score,
												self_text, trending, trending_level, twitter_media_url, connection,
												profile_pic_url, media_type, calcTrendingThreshold(twitter_handle, minutes_ago_utc, current_utc, connection));
									} else {
										media_type = "";
										twitter_media_url = "";
										System.out.println(
												" This post from " + username + " has a current engagement score of "
														+ current_engagement_score + " and is not trending.");
										addToSql(twitter_id, twitter_handle, twitter_name, created_utc, current_utc,
												minutes_ago_utc, permalink, current_engagement_score, self_text,
												trending, trending_level, twitter_media_url, connection,
												profile_pic_url, media_type, calcTrendingThreshold(twitter_handle, minutes_ago_utc, current_utc, connection));
										addToTweetCount(twitter_handle, connection);
									}
								} else {
									System.out.print(" This post from " + username + " is a retweet or a reply.");
								}
							} else {
								System.out.print(" The post from " + username + " is not between 10-20 minutes ago");
							}
						} else {
							System.out.print(" The post from " + username + " is already on the server!");
						}
					}
				}

			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	private boolean otherGamesMentioned(String self_text) {
		self_text = self_text.toLowerCase();
		if (self_text.contains("apex") || self_text.contains("fortnite") || self_text.contains("call of") || self_text.contains("csgo") || self_text.contains("r6") || self_text.contains("r6")) {
			return true;
		}
		return false;
	}
	public double getMedianTrendingLevel(Connection connection) throws Exception {
		PreparedStatement pStatement = connection
				.prepareStatement("SELECT trending_level FROM TwitterData WHERE trending=1");
		ResultSet resultSet = pStatement.executeQuery();
		double total = 0;
		int amount = 0;
		while (resultSet.next()) {
			// System.out.println(resultSet.getDouble("trending_level"));
			total += resultSet.getDouble("trending_level");
			amount++;
		}
		return total / (double) amount;
	}

	private Boolean isOnServer(long id, Connection connection) throws SQLException {

		PreparedStatement pStatement = connection.prepareStatement("SELECT * FROM TwitterData WHERE twitter_id=" + id);
		ResultSet resultSet = pStatement.executeQuery();
		int i = 0;
		while (resultSet.next()) {
			i++;
		}
		if (i == 0) {
			return false;
		} else {
			return true;
		}
	}

	private void addToTweetCount(String twitter_handle, Connection connection) {
		final String insert_statement = "UPDATE TwitterUsers SET tweet_count = tweet_count + 1 WHERE twitter_handle='"
				+ twitter_handle + "'";
		try {
			PreparedStatement pStatement = connection.prepareStatement(insert_statement);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addToTrendingTweetCount(String twitter_handle, Connection connection) {
		final String insert_statement = "UPDATE TwitterUsers SET trending_count = tweet_count + 1 WHERE twitter_handle='"
				+ twitter_handle + "'";
		try {
			PreparedStatement pStatement = connection.prepareStatement(insert_statement);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addToTrendingSql(long twitter_id, String twitter_handle, String twitter_name, int created_utc,
			int current_utc, int minutes_ago_utc, String permalink, int score, String self_text, Boolean trending,
			double trending_level, String twitter_media_url, Connection connection, String user_profile_pic_url,
			String media_type, double trending_threshold) throws SQLException {

		System.out.println("Adding post from " + twitter_name + " to trending server...");
		BigDecimal trending_level_bd = BigDecimal.valueOf(trending_level);
		final String insert_statement = "INSERT INTO TrendingRedditTwitter (twitter_id, reddit_id, twitter_handle, twitter_name, created_utc,"
				+ " current_utc, day_of_week, hour_of_day, reddit_author, minutes_ago_utc, permalink, score, score_per_minute, self_text, subreddit,"
				+ " title, trending, trending_level, url, twitter_media_url, user_profile_pic_url, media_type, trending_threshold)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement pStatement = connection.prepareStatement(insert_statement);
			pStatement.setLong(1, twitter_id);// twitter id
			pStatement.setString(2, "");// reddit id
			pStatement.setString(3, twitter_handle);
			pStatement.setString(4, twitter_name);
			pStatement.setInt(5, created_utc);
			pStatement.setInt(6, current_utc);
			pStatement.setString(7, "");// day of week
			pStatement.setInt(8, 0);// hour of day
			pStatement.setString(9, "");// reddit author
			pStatement.setInt(10, minutes_ago_utc);
			pStatement.setString(11, permalink);
			pStatement.setInt(12, score);
			pStatement.setDouble(13, 0);// score per minute
			pStatement.setString(14, self_text);
			pStatement.setString(15, "");// subreddit
			pStatement.setString(16, "");// title
			pStatement.setBoolean(17, trending);
			pStatement.setBigDecimal(18, trending_level_bd);
			pStatement.setString(19, "");
			if (twitter_media_url == null) {
				pStatement.setString(20, "");
			} else {
				pStatement.setString(20, twitter_media_url);
			}
			pStatement.setString(21, user_profile_pic_url);
			pStatement.setString(22, media_type);
			pStatement.setDouble(23, trending_threshold);
			pStatement.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.toString());
			System.out.println("ERROR ADDING DATA TO TRENDINGREDDITTWITTER");
		}

	}

	private void addToSql(long twitter_id, String twitter_handle, String twitter_name, int created_utc, int current_utc,
			int minutes_ago_utc, String permalink, int score, String self_text, Boolean trending, double trending_level,
			String twitter_media_url, Connection connection, String user_profile_pic_url, String media_type, double trending_threshold)
			throws SQLException {
		System.out
				.println("Adding post from " + twitter_name + " to regular server... Minutes ago: " + minutes_ago_utc);
		BigDecimal trending_level_bd = BigDecimal.valueOf(trending_level);
		final String insert_statement = "INSERT INTO TwitterData (twitter_id, twitter_handle, twitter_name, created_utc,"
				+ " current_utc, minutes_ago_utc, permalink, score, self_text, trending, trending_level, twitter_media_url, user_profile_pic_url, media_type, trending_threshold)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement pStatement = connection.prepareStatement(insert_statement);
			pStatement.setLong(1, twitter_id);
			pStatement.setString(2, twitter_handle);
			pStatement.setString(3, twitter_name);
			pStatement.setInt(4, created_utc);
			pStatement.setInt(5, current_utc);
			pStatement.setInt(6, minutes_ago_utc);
			pStatement.setString(7, permalink);
			pStatement.setInt(8, score);
			pStatement.setString(9, self_text);
			pStatement.setBoolean(10, trending);
			pStatement.setBigDecimal(11, trending_level_bd);
			if (twitter_media_url == null) {
				pStatement.setString(12, "");
			} else {
				pStatement.setString(12, twitter_media_url);
			}
			pStatement.setString(13, user_profile_pic_url);
			pStatement.setString(14, media_type);
			pStatement.setDouble(15, trending_threshold);
			pStatement.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.toString());
			System.out.println("ERROR ADDING TO TWITTER DATA");
		}
	}
	private double calcTrendingThreshold(String twitter_handle, int minutes_ago, int current_engagement_score, Connection connection) throws SQLException {
		PreparedStatement pStatement = connection.prepareStatement(
				"SELECT avg_total_engagement FROM TwitterUsers WHERE twitter_handle='" + twitter_handle + "'");
		ResultSet resultSet = pStatement.executeQuery();
		int avg_total_engagement;
		double trending_threshold = 0;
		while (resultSet.next()) {
			avg_total_engagement = resultSet.getInt("avg_total_engagement");
			switch (minutes_ago) {
			case 10:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.03, twitter_handle, connection);
				break;
			case 11:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.033, twitter_handle,
						connection);
				break;
			case 12:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.038, twitter_handle,
						connection);
				break;
			case 13:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.041, twitter_handle,
						connection);
				break;
			case 14:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.0435, twitter_handle,
						connection);
				break;
			case 15:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.045, twitter_handle,
						connection);
				break;
			case 16:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.048, twitter_handle,
						connection);
				break;
			case 17:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.051, twitter_handle,
						connection);
				break;
			case 18:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.054, twitter_handle,
						connection);
				break;
			case 19:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.057, twitter_handle,
						connection);
				break;
			case 20:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.06, twitter_handle, connection);
				break;
			default:
				trending_threshold = 0;
			}
		}
		return trending_threshold;
	}
	private boolean isTrending(String twitter_handle, int minutes_ago, int current_engagement_score,
			Connection connection) throws SQLException {
		// System.out.println("Checking to see if post is trending...");
		PreparedStatement pStatement = connection.prepareStatement(
				"SELECT avg_total_engagement FROM TwitterUsers WHERE twitter_handle='" + twitter_handle + "'");
		ResultSet resultSet = pStatement.executeQuery();
		int avg_total_engagement;
		double trending_threshold;
		while (resultSet.next()) {
			avg_total_engagement = resultSet.getInt("avg_total_engagement");
			switch (minutes_ago) {
			case 10:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.03, twitter_handle, connection);
				break;
			case 11:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.033, twitter_handle,
						connection);
				break;
			case 12:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.038, twitter_handle,
						connection);
				break;
			case 13:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.041, twitter_handle,
						connection);
				break;
			case 14:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.0435, twitter_handle,
						connection);
				break;
			case 15:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.045, twitter_handle,
						connection);
				break;
			case 16:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.048, twitter_handle,
						connection);
				break;
			case 17:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.051, twitter_handle,
						connection);
				break;
			case 18:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.054, twitter_handle,
						connection);
				break;
			case 19:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.057, twitter_handle,
						connection);
				break;
			case 20:
				trending_threshold = calculateTrendingThreshold(avg_total_engagement, 0.06, twitter_handle, connection);
				break;
			default:
				trending_threshold = 0;
			}
			if ((double) current_engagement_score >= trending_threshold && trending_threshold != 0) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	private double getUsersTrendingAverage(String twitter_handle, Connection connection) throws SQLException {
		double trending_average = 0;
		int average_at_20 = getEngagementAverageAt20(connection, twitter_handle);
		Boolean is_a_team_account = getTwitterType(connection, twitter_handle);
		try {
			PreparedStatement pStatement = connection
					.prepareStatement("SELECT tweet_count,trending_count FROM TwitterUsers WHERE twitter_handle='"
							+ twitter_handle + "'");
			ResultSet resultSet = pStatement.executeQuery();

			while (resultSet.next()) {
				int tweet_count = resultSet.getInt("tweet_count");
				int trending_count = resultSet.getInt("trending_count");
				if (tweet_count == 0 || trending_count == 0) {
					System.out.print("Trending_count or tweet_count is 0");
					return getTrendingMultiplier(0, is_a_team_account, average_at_20);
				}
				trending_average = (double) trending_count / (double) tweet_count;
			}
		} catch (SQLException e) {
			System.out.println("ERROR GETTING USER TRENDING AVERAGE");
			System.out.println(e.toString());
			return getTrendingMultiplier(0, is_a_team_account, average_at_20);
		}
		System.out.print(
				"Trending multiplier is: " + getTrendingMultiplier(trending_average, is_a_team_account, average_at_20));
		return getTrendingMultiplier(trending_average, is_a_team_account, average_at_20);
	}

	private double getTrendingMultiplier(double trending_average, Boolean is_a_team_account, int avg_at_20) {
		double multiplier = 0;
		if (is_a_team_account) {
			multiplier = multiplier + 3.5;
		}

		if (avg_at_20 > 0 && avg_at_20 <= 10) {
			multiplier = multiplier + 1.3;
		} else if (avg_at_20 >= 11 && avg_at_20 < 15) {
			multiplier = multiplier + .7;
		} else if (avg_at_20 >= 15 && avg_at_20 <= 20) {
			multiplier = multiplier + .5;
		}

		if (trending_average == 1) {
			return multiplier = multiplier + 5;
		} else if (trending_average > 0 && trending_average < .09) {
			return multiplier = multiplier + -.2;
		} else if (trending_average > .1 && trending_average < .19) {
			return multiplier = multiplier + -.1;
		} else if (trending_average > .2 && trending_average < .29) {
			return multiplier = multiplier + 0;
		} else if (trending_average > .3 && trending_average < .39) {
			return multiplier = multiplier + .2;
		} else if (trending_average > .4 && trending_average < .49) {
			return multiplier = multiplier + .3;
		} else if (trending_average > .5 && trending_average < .59) {
			return multiplier = multiplier + .5;
		} else if (trending_average > .6 && trending_average < .69) {
			return multiplier = multiplier + 1;
		} else if (trending_average > .7 && trending_average < .79) {
			return multiplier = multiplier + 1.5;
		} else if (trending_average > .8 && trending_average < .89) {
			return multiplier = multiplier + 2;
		} else if (trending_average > .9 && trending_average < .99) {
			return multiplier = multiplier + 4;
		} else if (trending_average == 0) {
			return multiplier = multiplier + -.4;
		}
		return multiplier = multiplier + 0;
	}

	private double calculateTrendingThreshold(int avg_total_engagement, double percentage, String twitter_handle,
			Connection connection) throws SQLException {
		double trending_threshold = 0;
		compare_score = avg_total_engagement * percentage;// if their average total engagement for a tweet is 1000 and
															// percentage is is 6% at 20, then its 1000*0.06 = 60
		trending_threshold = compare_score * (1.7 + getUsersTrendingAverage(twitter_handle, connection));// 60*2 = 120.
																										// post
																										// must be at
																										// 120 to be
																										// considered
																										// trending
		System.out.print(" Trending Threshold is: " + trending_threshold);
		return trending_threshold;
	}

	public ArrayList<String> getTwitterHandles() throws Exception {
		ArrayList<String> twitter_handles = new ArrayList<>();
		PreparedStatement pStatement = getConnection().prepareStatement("SELECT twitter_handle FROM TwitterUsers");
		ResultSet resultSet = pStatement.executeQuery();

		while (resultSet.next()) {
			twitter_handles.add(resultSet.getString("twitter_handle"));
		}
		return twitter_handles;

	}

	private int getEngagementAverageAt20(Connection connection, String twitter_handle) throws SQLException {
		int average_at_20 = 0;
		PreparedStatement pStatement = connection
				.prepareStatement("SELECT avg_at_20 FROM TwitterUsers WHERE twitter_handle='" + twitter_handle + "'");
		ResultSet resultSet = pStatement.executeQuery();
		while (resultSet.next()) {
			average_at_20 = resultSet.getInt("avg_at_20");
		}
		return average_at_20;
	}

	private Boolean getTwitterType(Connection connection, String twitter_handle) throws SQLException {
		boolean is_a_team_account = false;
		PreparedStatement pStatement = connection
				.prepareStatement("SELECT type FROM TwitterUsers WHERE twitter_handle='" + twitter_handle + "'");
		ResultSet resultSet = pStatement.executeQuery();
		while (resultSet.next()) {
			if (resultSet.getString("type").matches("Team")) {
				System.out.print(" -This is a team account- ");
				is_a_team_account = true;
			}
		}

		return is_a_team_account;
	}

	private String getTwitterName(Connection connection, String twitter_handle) throws SQLException {
		// SELECT NAME FROM TwitterUsers WHERE user_handle='tldoublelift';
		// System.out.println("Getting twitter name for " + twitter_handle);
		String twitter_name = null;
		PreparedStatement pStatement = connection
				.prepareStatement("SELECT name FROM TwitterUsers WHERE twitter_handle='" + twitter_handle + "'");
		ResultSet resultSet = pStatement.executeQuery();
		while (resultSet.next()) {
			twitter_name = resultSet.getString("name");
		}
		return twitter_name;
	}

	public String getRegionFromTwitterUser(String handle, Connection connection) throws Exception {
		String region = null;
		PreparedStatement pStatement = connection
				.prepareStatement("SELECT region FROM TwitterUsers WHERE twitter_handle='" + handle + "'");
		ResultSet resultSet = pStatement.executeQuery();
		while (resultSet.next()) {
			region = resultSet.getString("region");
		}
		return region;
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

	public void getTweetAverageForTwitterUser(Connection connection, twitter4j.Twitter twitter, String username)
			throws TwitterException {
		List<Status> statuses;
		Paging p = new Paging();
		p.count(45);
		statuses = twitter.getUserTimeline(username, p);
		if (statuses != null) {
			for (Status status : statuses) {
				String text = status.getText();
				if (!Arrays.toString(status.getURLEntities()).contains("expandedURL=")) {
					if (status.getInReplyToScreenName() == null && !text.contains("RT @")) {
						System.out.print(" Not a reply, Adding..... " + text);
					} else {
						System.out.print(" This tweet is a reply ");
					}
				} else {
					System.out.print(" This tweet from  " + username + " contains a link ");
					// System.out.println("Found one lol");
				}
			}
		}

	}

	public void addTwitterUsersToServer(String handle, Connection connection, twitter4j.Twitter twitter)
			throws SQLException, Exception {
		final String insert_statement = "INSERT INTO TwitterUsers (twitter_handle, avg_total_engagement, avg_at_20)"
				+ " VALUES (?,?,?)";
		PreparedStatement pStatement = connection.prepareStatement(insert_statement);
		int average_total_engagement = getAverageTotalEngagement(handle, connection, twitter);
		int average_at_20 = (int) Math.round(average_total_engagement * .06);
		pStatement.setString(1, handle);
		pStatement.setInt(2, average_total_engagement);
		pStatement.setInt(3, average_at_20);
		pStatement.executeUpdate();
		System.out.println("Finished adding " + handle);
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
						System.out.print(" Not a reply, Adding..... " + text);
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
}
