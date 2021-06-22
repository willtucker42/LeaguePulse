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
	
		Connection connection = getConnection();
		double total = 0;
		int amount = 0;
		GatherTwitterData twitterAverages = new GatherTwitterData();
		twitterAverages.start(connection);
		
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
