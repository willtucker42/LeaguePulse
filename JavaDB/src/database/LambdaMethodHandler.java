package database;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.redditgetter.Reddit;

public class LambdaMethodHandler implements RequestHandler<String, String>{
	public String handleRequest(String input, Context context) {
		Reddit reddit = new Reddit();
		try {
			//FirebaseSendMessage firebaseSendMessage = new FirebaseSendMessage();
			//firebaseSendMessage.sendNotification("from aws", "title");
			System.out.println("Retrieving reddit data... Version 2.5 - trending thresholds changedd");
			//not deployed 1.4 yet
			reddit.getLeagueOfLegendsRising();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Hello World - " + input;
	}
}
