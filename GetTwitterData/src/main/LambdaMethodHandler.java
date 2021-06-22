package main;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import Twitter.TwitterUtils;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class LambdaMethodHandler implements RequestHandler<String, String> {
	public String handleRequest(String input, Context context) {
		System.out.println(
				"Running GetTwitterData 2.11 changed trends");
		try {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true).setOAuthConsumerKey("Ncv9BXh9l6CncEcvK5DVHlpMN")
					.setOAuthConsumerSecret("v8wFMrKkPSVQ8CZ691qqm1NupV824dkuqPCdbgKkqq1ipVyPVw")
					.setOAuthAccessToken("846952608094978048-eYvaZTiiz0VciCVcEDtFG4PaWgYMBhZ")
					.setOAuthAccessTokenSecret("3AedXLphMHPjmJkmS5GF2dxbtmEwWEX4gyfI3O2QOhJMK");
			Date date = new Date();
			TwitterFactory twitterFactory = new TwitterFactory(cb.build());
			twitter4j.Twitter twitter = twitterFactory.getInstance();
			twitter.getRateLimitStatus();
			List<Status> statuses = null;
			Paging p = new Paging();
			p.count(1);
			TwitterUtils twitterUtils = new TwitterUtils();
			Connection connection = twitterUtils.getConnection();
			double median_twitter_trending_level = twitterUtils.getMedianTrendingLevel(connection);
			for (String twitter_handle : twitterUtils.getTwitterHandles()) {
				twitterUtils.checkUserForTrendingTweets(twitter_handle, connection, twitter, statuses, p, date, median_twitter_trending_level);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Hello World - " + input;
	}
}
