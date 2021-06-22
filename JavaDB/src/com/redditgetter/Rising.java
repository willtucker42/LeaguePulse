package com.redditgetter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.parse.ParseException;
import com.redditgetter.DataModel.Feed;
import com.redditgetter.DataModel.children.Children;

public class Rising {
	final static String RDS_HOSTNAME = "reddit-database.cx9mmdexfpd7.us-west-1.rds.amazonaws.com";
	final static String RDS_PORT = "3306";
	final static String RDS_DB_NAME = "reddit-database";
	final static String RDS_USERNAME = "willtucker42";
	final static String RDS_PASSWORD = "xxxxxx";
	private Time time = new Time();
	private long currentUnixTime = System.currentTimeMillis() / 1000L;

	public void r_LeagueOfLegends(Retrofit retrofit, String day_hour, final double average_score,
			double median_trending_level) throws Exception {
		// System.out.println("In get r_LeagueOfLegends");
		RedditAPI redditAPI = retrofit.create(RedditAPI.class);
		Call<Feed> call = redditAPI.getData();
		Response<Feed> response = call.execute();
		getData(response, average_score, median_trending_level);
		// System.out.println("Here");
		System.out.println("Finished getting Reddit data.");
	}

	private void getData(Response<Feed> response, double average_score, double median_trending_level) throws Exception {
		System.out.println("onResponse: Server Response: " + response.toString());
		assert response.body() != null;
		// Log.d(TAG, "onResponse: received information: " +
		// response.body().toString());
		ArrayList<Children> childrenList = response.body().getData().getChildren();

		for (int i = 0; i < childrenList.size(); i++) {
			Connection connection = getConnection();
			RedditDataServer redditDataServer = new RedditDataServer();
			FirebaseSendMessage firebaseSendMessage = new FirebaseSendMessage();
			long createdUTC = childrenList.get(i).getData().getCreated_utc();
			int minutes_ago_utc = (int) ((currentUnixTime - createdUTC) / 60);
			time.getHourPosted(createdUTC);
			double score_per_minute = (double) childrenList.get(i).getData().getScore() / minutes_ago_utc;
			String id = childrenList.get(i).getData().getId();
			String url = childrenList.get(i).getData().getUrl();
			String title = childrenList.get(i).getData().getTitle();
			String author = childrenList.get(i).getData().getAuthor();
			String permalink = childrenList.get(i).getData().getPermalink();
			String self_text = childrenList.get(i).getData().getSelftext();
			int score = childrenList.get(i).getData().getScore();
			String subreddit = childrenList.get(i).getData().getSubreddit();
			int created_utc = (int) childrenList.get(i).getData().getCreated_utc();
			int current_utc = (int) currentUnixTime;
			double trending_level = score_per_minute / average_score;
			// idQuery.whereEqualTo("id",childrenList.get(i).getData().getId());
			try {
				if (checkForIdMatch(id, connection) == true) {
					System.out.println("Found a matching record for id " + id);
				} else {
					if (score_per_minute <= 0.01) {
						System.out.println("No matching record found but score is too low for id: " + id);
						// score is below threshold to be added to db
					} else {
						System.out.println("No matching record found for " + id + ", adding to server.");
						if (self_text.equals("") || self_text == null) {// means this post has a direct link and no
																		// self_text; trending threshold should be more
																		// leniant
							pushDirectLinkPostToServer(id, title, author, score, subreddit, created_utc, current_utc,
									minutes_ago_utc, score_per_minute, url, permalink, self_text, trending_level,
									connection, average_score, firebaseSendMessage, redditDataServer,
									median_trending_level);

						} else {// means this post has self_text and no direct link. the trending threshold
								// should be more stringent
							pushSelfTextPostToServer(id, title, author, score, subreddit, created_utc, current_utc,
									minutes_ago_utc, score_per_minute, url, permalink, self_text, trending_level,
									connection, average_score, firebaseSendMessage, redditDataServer,
									median_trending_level);

						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}
	}

	private void pushDirectLinkPostToServer(String id, String title, String author, int score, String subreddit,
			int created_utc, int current_utc, int minutes_ago_utc, double score_per_minute, String url,
			String permalink, String self_text, double trending_level, Connection connection, double average_score,
			FirebaseSendMessage firebaseSendMessage, RedditDataServer redditDataServer, double median_trending_level)
			throws ParseException {
		if (((double) score > average_score * 13 && score >= 13) || trending_level > 13) {
			System.out.println("Title: " + title);
			String alert_identifier = null;
			if (trending_level >= 10) {
				alert_identifier = "type:reddit trending:top";
			} else {
				alert_identifier = "type:reddit trending:all";
			}
			// firebaseSendMessage.sendNotification("League Pulse Trending", title,
			// alert_identifier);
			redditDataServer.addRedditInfoToSql(id, title, author, score, subreddit, created_utc, current_utc,
					minutes_ago_utc, score_per_minute, url, permalink, true, self_text, trending_level, connection,
					alert_identifier);
		} else {

			if (!(minutes_ago_utc < 40)) {
				redditDataServer.addRedditInfoToSql(id, title, author, score, subreddit, created_utc, current_utc,
						minutes_ago_utc, score_per_minute, url, permalink, false, self_text, trending_level, connection,
						"");
			} else {
				System.out.println("Post does not meet trending threshhold. Not adding to server yet");
			}
		}

	}

	private void pushSelfTextPostToServer(String id, String title, String author, int score, String subreddit,
			int created_utc, int current_utc, int minutes_ago_utc, double score_per_minute, String url,
			String permalink, String self_text, double trending_level, Connection connection, double average_score,
			FirebaseSendMessage firebaseSendMessage, RedditDataServer redditDataServer, double median_trending_level)
			throws ParseException {
		System.out.println("SelfText Post");

		if ((double) score > average_score * 18 && score >= 20) {
			String alert_identifier = null;

			if ((self_text.contains("---\n\n###MATCH") || self_text.contains("---\n\n### MATCH"))
					&& self_text.contains("postmatch.team")) {
				alert_identifier = "type:match";

			} else {
				if (trending_level >= median_trending_level) {
					alert_identifier = "type:reddit trending:top";
				} else {
					alert_identifier = "type:reddit trending:all";
				}
			}
			// firebaseSendMessage.sendNotification("League Pulse Trending", title,
			// alert_identifier);
			if (!self_text.contains("tbd")) {
				redditDataServer.addRedditInfoToSql(id, title, author, score, subreddit, created_utc, current_utc,
						minutes_ago_utc, score_per_minute, url, permalink, true, self_text, trending_level, connection,
						alert_identifier);
			}
		} else {
			if (!(minutes_ago_utc < 40)) {
				redditDataServer.addRedditInfoToSql(id, title, author, score, subreddit, created_utc, current_utc,
						minutes_ago_utc, score_per_minute, url, permalink, false, self_text, trending_level, connection,
						"");
			} else {
				System.out.println("Post does not meet trending threshhold. Not adding to server yet");
			}
		}
	}

	private static boolean checkForIdMatch(String id, Connection connection) throws Exception {
		// System.out.println("In get checkForIdMatch");
		try {
			PreparedStatement pStatement = connection
					.prepareStatement("SELECT id FROM RedditData WHERE id='" + id + "'");
			ResultSet resultSet = pStatement.executeQuery();

			ArrayList<String> arrayList = new ArrayList<String>();
			// System.out.println("The size of the result set is: " +
			// resultSet.getFetchSize());
			while (resultSet.next()) {
				arrayList.add(resultSet.getString("id"));
				System.out.println(resultSet.getString("id"));
			}
			if (arrayList.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return true;
	}

	private static Connection getConnection() throws Exception {
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
