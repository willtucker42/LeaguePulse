import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RedditHourlyAverages {
	final static String RDS_HOSTNAME = "reddit-database.cx9mmdexfpd7.us-west-1.rds.amazonaws.com";
	final static String RDS_PORT = "3306";
	final static String RDS_DB_NAME = "reddit-database";
	final static String RDS_USERNAME = "willtucker42";
	final static String RDS_PASSWORD = "Createaccou1090";
    private double avg_0_1 = 0, avg_2_4 = 0, avg_5_7 = 0, avg_8_10 = 0, avg_11_13 = 0, avg_14_16 = 0,
            avg_17_19 = 0, avg_20_23 = 0;

    private int count_11_13 = 0, count_14_16 = 0, count_17_19 = 0, count_20_23 = 0, count_0_1 = 0,
            count_2_4 = 0, count_5_7 = 0, count_8_10 = 0;
	public String startUpdate(Connection connection, String day_of_week) throws Exception {
		PreparedStatement pStatement = connection.prepareStatement("SELECT hour_of_day, score_per_minute FROM RedditData WHERE day_of_week='"+day_of_week+"'");
		ResultSet resultSet = pStatement.executeQuery();
		while(resultSet.next()) {
			getHourlyAverages(resultSet.getInt("hour_of_day"), resultSet.getDouble("score_per_minute"));
		} 
		createAverages();
		//sendAveragesToServer(day_of_week, connection);
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
	private void sendAveragesToServer(String day_of_week, Connection connection) throws Exception {
		if(day_of_week.equals("Monday")) {
			final String insert_statement = "INSERT INTO HourlyAverages (mon_0_1, mon_2_4, mon_5_7, mon_8_10, mon_11_13,"
					+ " mon_14_16, mon_17_19, mon_20_23)"
					+ " VALUES (?,?,?,?,?,?,?,?)";
			PreparedStatement pStatement = connection.prepareStatement(insert_statement);
			executeStatement(pStatement);
		}else if(day_of_week.equals("Tuesday")) {
			final String insert_statement = "INSERT INTO HourlyAverages (tue_0_1, tue_2_4, tue_5_7, tue_8_10, tue_11_13,"
					+ " tue_14_16, tue_17_19, tue_20_23)"
					+ " VALUES (?,?,?,?,?,?,?,?)";
			PreparedStatement pStatement = connection.prepareStatement(insert_statement);
			executeStatement(pStatement);
			
		}else if(day_of_week.equals("Wednesday")) {
			final String insert_statement = "INSERT INTO HourlyAverages (wed_0_1, wed_2_4, wed_5_7, wed_8_10, wed_11_13,"
					+ " wed_14_16, wed_17_19, wed_20_23)"
					+ " VALUES (?,?,?,?,?,?,?,?)";
			PreparedStatement pStatement = connection.prepareStatement(insert_statement);
			executeStatement(pStatement);
			
		}else if(day_of_week.equals("Thursday")) {
			final String insert_statement = "INSERT INTO HourlyAverages (thu_0_1, thu_2_4, thu_5_7, thu_8_10, thu_11_13,"
					+ " thu_14_16, thu_17_19, thu_20_23)"
					+ " VALUES (?,?,?,?,?,?,?,?)";
			PreparedStatement pStatement = connection.prepareStatement(insert_statement);
			executeStatement(pStatement);
			
		}else if(day_of_week.equals("Friday")) {
			final String insert_statement = "INSERT INTO HourlyAverages (fri_0_1, fri_2_4, fri_5_7, fri_8_10, fri_11_13,"
					+ " fri_14_16, fri_17_19, fri_20_23)"
					+ " VALUES (?,?,?,?,?,?,?,?)";
			PreparedStatement pStatement = connection.prepareStatement(insert_statement);
			executeStatement(pStatement);
			
		}else if(day_of_week.equals("Saturday")) {
			final String insert_statement = "INSERT INTO HourlyAverages (sat_0_1, sat_2_4, sat_5_7, sat_8_10, sat_11_13,"
					+ " sat_14_16, sat_17_19, sat_20_23)"
					+ " VALUES (?,?,?,?,?,?,?,?)";
			PreparedStatement pStatement = connection.prepareStatement(insert_statement);
			executeStatement(pStatement);
			
		}else if(day_of_week.equals("Sunday")) {
			final String insert_statement = "INSERT INTO HourlyAverages (sun_0_1, sun_2_4, sun_5_7, sun_8_10, sun_11_13,"
					+ " sun_14_16, sun_17_19, sun_20_23)"
					+ " VALUES (?,?,?,?,?,?,?,?)";
			PreparedStatement pStatement = connection.prepareStatement(insert_statement);
			executeStatement(pStatement);
			
		}
	}
	private void executeStatement(PreparedStatement pStatement) throws Exception {
		pStatement.setBigDecimal(1, BigDecimal.valueOf(avg_0_1));
		pStatement.setBigDecimal(2, BigDecimal.valueOf(avg_2_4));
		pStatement.setBigDecimal(3, BigDecimal.valueOf(avg_5_7));
		pStatement.setBigDecimal(4, BigDecimal.valueOf(avg_8_10));
		pStatement.setBigDecimal(5, BigDecimal.valueOf(avg_11_13));
		pStatement.setBigDecimal(6, BigDecimal.valueOf(avg_14_16));
		pStatement.setBigDecimal(7, BigDecimal.valueOf(avg_17_19));
		pStatement.setBigDecimal(8, BigDecimal.valueOf(avg_20_23));
		pStatement.executeUpdate();
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
        	System.out.println("ERROR");
           // Log.e(TAG, "getHourlyAverage: There was an error getting hour_of_day");
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
