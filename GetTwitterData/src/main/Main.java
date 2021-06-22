package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PrimitiveIterator.OfDouble;

import javax.print.attribute.standard.Media;

import Twitter.GatherTwitterData;
import Twitter.TwitterUtils;
import firebase.FirebaseSendMessage;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

public class Main {
	final static String RDS_HOSTNAME = "reddit-database.cx9mmdexfpd7.us-west-1.rds.amazonaws.com";
	final static String RDS_PORT = "3306";
	final static String RDS_DB_NAME = "reddit-database";
	final static String RDS_USERNAME = "willtucker42";
	final static String RDS_PASSWORD = "xxxxxxxxx";

	public static void main(String[] args) throws SQLException, Exception {
		/*ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("Ncv9BXh9l6CncEcvK5DVHlpMN")
				.setOAuthConsumerSecret("v8wFMrKkPSVQ8CZ691qqm1NupV824dkuqPCdbgKkqq1ipVyPVw")
				.setOAuthAccessToken("846952608094978048-eYvaZTiiz0VciCVcEDtFG4PaWgYMBhZ")
				.setOAuthAccessTokenSecret("3AedXLphMHPjmJkmS5GF2dxbtmEwWEX4gyfI3O2QOhJMK");
		Date date = new Date();
		TwitterFactory twitterFactory = new TwitterFactory(cb.build());
		twitter4j.Twitter twitter = twitterFactory.getInstance();
		List<Status> statuses = null;
		Paging p = new Paging();
		p.count(1);
		TwitterUtils twitterUtils = new TwitterUtils();*/
		Connection connection = getConnection();
		double total = 0;
		int amount = 0;
		GatherTwitterData twitterAverages = new GatherTwitterData();
		twitterAverages.start(connection);
		//System.out.println("Get median trending level: " + twitterUtils.getMedianTrendingLevel(connection));
		/*PreparedStatement pStatement = connection.prepareStatement("SELECT trending_level FROM TwitterData WHERE trending=1");
		ResultSet resultSet = pStatement.executeQuery();
		while(resultSet.next()) {
			System.out.println(resultSet.getDouble("trending_level"));
			total +=resultSet.getDouble("trending_level");
			amount++;
		}*/
		//System.out.println("Average trending_level for trending posts is: " + total / (double) amount);
		/*
		 * statuses = twitter.getUserTimeline("tsm",p); if (statuses!=null) { for(Status
		 * status: statuses) { System.out.println("Regular: " +
		 * status.getUser().getProfileImageURL()); System.out.println("400x400: " +
		 * status.getUser().get400x400ProfileImageURL()); System.out.println("Regular: "
		 * + status.getUser().getBiggerProfileImageURL());
		 * 
		 * } }
		 */
		
		/*ArrayList<String> new_twitter_users = new ArrayList<>();
		
		for (String handle : new_twitter_users) {
			twitterUtils.addTwitterUsersToServer(handle, connection, twitter);
		}*/
		/*
		 * for (String twitter_handle : twitterUtils.getTwitterHandles()) {
		 * twitterUtils.checkUserForTrendingTweets(twitter_handle, connection, twitter,
		 * statuses, p, date); }
		 */
		/*
		 * statuses = twitter.getUserTimeline("jacketienne", p); if (statuses != null) {
		 * for (Status status : statuses) { System.out.println(status.getText());
		 * MediaEntity[] media = status.getMediaEntities();
		 * System.out.println(media.length); for (MediaEntity m : media) {
		 * MediaEntity.Variant[] variant = m.getVideoVariants(); if
		 * (m.getType().equals("video")) { System.out.println("FIRST VARIANT url: " +
		 * variant[0].getUrl()); for (int j = 0; j < variant.length; j++) {
		 * System.out.println("VARIANT url: " + variant[j].getUrl()); } } else if
		 * (m.getType().equals("animated_gif")) {
		 * System.out.println("ANIMATED GIF url: " + variant[0].getUrl()); } else {
		 * System.out.println("Other type is: " + m.getType().toString()); }
		 * System.out.println(m.getMediaURL()); }
		 * System.out.println(status.getUser().getProfileImageURL()); } }
		 */

		// getcount();
		// twitterUtils.checkUserForTrendingTweets("sneaky",
		// twitterUtils.getConnection(), twitter, statuses, p, date);
		/*
		 * statuses = twitter.getUserTimeline("realDonaldTrump", p); if (statuses !=
		 * null) { for (Status status : statuses) { MediaEntity[] media =
		 * status.getMediaEntities(); String text = status.getText();
		 * System.out.println("id: " + status.getId());
		 * System.out.println("Favorite count is: " + status.getFavoriteCount());
		 * System.out.println("Text: " + text); System.out.println("UTC stamp is: " +
		 * status.getCreatedAt().getTime() / 1000); System.out.println("Media Length: "
		 * + media.length); long time1 =
		 * (date.getTime()/1000)-(status.getCreatedAt().getTime() / 1000); int
		 * minutes_ago = (int)time1/60; System.out.println("Minutes ago is... " +
		 * minutes_ago); if (media.length != 0) { for (MediaEntity m : media) {
		 * System.out.println("Url is: " + m.getMediaURL()); } } } } System.out.println(
		 * "-----------------------------------------------------------------------------------------------------------------------------------"
		 * ); statuses = null; statuses = twitter.getUserTimeline("tldoublelift", p); if
		 * (statuses != null) { for (Status status : statuses) { MediaEntity[] media =
		 * status.getMediaEntities(); String text = status.getText();
		 * System.out.println("id: " + status.getId());
		 * System.out.println("Favorite count is: " + status.getFavoriteCount());
		 * System.out.println("Text: " + text); System.out.println("UTC stamp is: " +
		 * status.getCreatedAt().getTime() / 1000); System.out.println("Media Length: "
		 * + media.length); if (media.length != 0) { for (MediaEntity m : media) {
		 * System.out.println("Url is: " + m.getMediaURL()); } }
		 * 
		 * int time1 = (int) (((date.getTime()/1000)-(status.getCreatedAt().getTime() /
		 * 1000))/60); System.out.println("Minutes ago is... " + time1); } }
		 * System.out.println("Rate limit status is: " + twitter.getRateLimitStatus());
		 * double score = 1421 * .03; double added = score * 3;
		 * System.out.println("score : " + score); System.out.println("added : " +
		 * added);
		 */

	}

	public static void getcount() throws SQLException, Exception {
		PreparedStatement pStatement = getConnection()
				.prepareStatement("SELECT * FROM TwitterData WHERE twitter_id=125469696854772301");
		ResultSet resultSet = pStatement.executeQuery();
		int i = 0;
		while (resultSet.next()) {
			i++;
		}
		System.out.println(i);

	}

	public static Connection getConnection() throws Exception {
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

	public String getTwitterName(Connection connection, String twitter_handle) throws SQLException {
		// SELECT NAME FROM TwitterUsers WHERE user_handle='tldoublelift';
		String twitter_name = null;
		PreparedStatement pStatement = connection
				.prepareStatement("SELECT name FROM TwitterUsers WHERE user_handle=" + twitter_handle);
		ResultSet resultSet = pStatement.executeQuery();
		while (resultSet.next()) {
			twitter_name = resultSet.getString("name");
		}
		return twitter_name;
	}
}
