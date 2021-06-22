package database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.redditgetter.FirebaseSendMessage;

public class Main {
	final static String RDS_HOSTNAME = "reddit-database.cx9mmdexfpd7.us-west-1.rds.amazonaws.com";
	final static String RDS_PORT = "3306";
	final static String RDS_DB_NAME = "reddit-database";
	final static String RDS_USERNAME = "willtucker42";
	final static String RDS_PASSWORD = "Createaccou1090";
	final static String id = "bq5mt8";
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//createTable();
		//addData();
		//getData();

		//Reddit reddit = new Reddit();
		//reddit.getLeagueOfLegendsRising();
		FirebaseSendMessage firebaseSendMessage = new FirebaseSendMessage();
		firebaseSendMessage.sendNotification("message test", "title test"," alert_identifier");
	}
	public static void getData()throws Exception{
		try {
			Connection connection = getConnection();
			PreparedStatement pStatement = connection.prepareStatement("SELECT id FROM RedditData WHERE id='" + id + "'");
			ResultSet resultSet = pStatement.executeQuery();
			
			ArrayList<String> arrayList = new ArrayList<String>();
			System.out.println("The size of the result set is: " + resultSet.getFetchSize());
			while(resultSet.next()) {
				arrayList.add(resultSet.getString("id"));
				System.out.println(resultSet.getString("id"));
			}
			if(arrayList.size()>0) {
				System.out.println("We found a matching record.");
			}else {
				System.out.println("No matching record found");
			}
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	public static void addData()throws Exception{
		BigDecimal score_per_minute = BigDecimal.valueOf(0.11764705882352941);
		BigDecimal trending_level = BigDecimal.valueOf(3.3508179637314965);
		final String insert_statement = "INSERT INTO RedditData (id, title, author, score, score_per_minute,"
				+ " minutes_ago_utc, trending_level, hour_of_day, day_of_week, url, trending, created_utc, current_utc, subreddit, permalink, self_text)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
		Connection connection = getConnection();
		PreparedStatement pStatement = connection.prepareStatement(insert_statement);
		pStatement.setString(1, "bryps7");//id
		pStatement.setString(2, "What champion would you love to play but you don't because of its role?");//title
		pStatement.setString(3, "zzinolol");//author
		pStatement.setInt(4, 2);//score
		pStatement.setBigDecimal(5, score_per_minute);//score_per_minute
		pStatement.setInt(6, 17);//minutes_ago_utc
		pStatement.setBigDecimal(7, trending_level);//trending_level
		pStatement.setInt(8, 21);//hour_of_day
		pStatement.setString(9, "Wednesday");//day_of_week
		pStatement.setString(10, "https://www.reddit.com/r/leagueoflegends/comments/brypsc/what_champion_would_you_love_to_play_but_you_dont/");//url
		pStatement.setBoolean(11, false);//trending
		pStatement.setInt(12, 1558584850);//created_utc
		pStatement.setInt(13, 1558585915);//current_utc
		pStatement.setString(14, "leagueoflegends");//subreddit
		pStatement.setString(15, "https://www.reddit.com/r/leagueoflegends/comments/brypsc/what_champion_would_you_love_to_play_but_you_dont/");//permalink
		pStatement.setString(16, "I've been playing lol for 8 years so I'm pretty bored of playing the same champs over and over again, so I've tried a few times to have fun with those that I like but outside of their main role, like Shaco mid/top, Hec top, Lee mid, etc." + 
				"Most of them were maybe viable a few years ago, but right now it's basically impossible to take a champ out of it's intended role, so... what are the champions that you would love to play if they were viable outside of their role?");
		pStatement.executeUpdate();
		}catch (Exception e) {
			System.out.println(e);
		}finally {
			System.out.println("Finished adding data");
		}
		
	}
	public static void createTable() throws Exception{
		try {
			Connection connection = getConnection();
			PreparedStatement create = connection.prepareStatement("CREATE TABLE IF NOT EXISTS reddit_database("
					+ "id VARCHAR(7), title TEXT(1000), author VARCHAR(100),"
					+ "score INT(7), score_per_minute DECIMAL(40,25), minutes_ago_utc INT(5),"
					+ "trending_level DECIMAL(40,25), hour_of_day INT(2), day_of_week VARCHAR(9),"
					+ "url VARCHAR(120), trending BOOLEAN, created_utc INT(10),"
					+ "current_utc INT(10), subreddit VARCHAR(25), permalink VARCHAR(150),"
					+ "self_text TEXT(50000), PRIMARY KEY(id))");
			create.executeUpdate();
			
		}catch (Exception e) {
			System.out.println("ERROR: "+ e);
		}finally {
			System.out.println("Function Complete.");
		}
	}
	
	
	public static Connection getConnection() throws Exception {
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			Class.forName(driver);
			String jdbcUrl = "jdbc:mysql://" + RDS_HOSTNAME + ":" + RDS_PORT + "/" + RDS_DB_NAME + "?user=" + RDS_USERNAME + "&password=" + RDS_PASSWORD;
			Connection connection = DriverManager.getConnection(jdbcUrl);
			System.out.println("Connected");
			return connection;
		} catch (Exception e) {
			System.out.println("ERROR: " + e);
		}

		return null;
	}

}
