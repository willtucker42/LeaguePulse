package firebase;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirebaseSendMessage {

    public String sendNotification(String message, String title, String alert_identifier) {
        //reads the text/xml/json from a URL 1 character at a time and returns a long string passed to onPostExecute()
        String url = "https://fcm.googleapis.com/fcm/send";
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization",
                    "key=AAAA8Ggg3AY:APA91bEqbJzBy358liAK4ao3MRzk_NNc6WbOQnExqYASNQ25X2ozlazh6dShJZO"
                    + "h94U6UUtdr_aaSXNABNcYb8X9cYQWzRVF5Ly9J8qxKZYDo1v0Ahvn5uZuenNtQNg0AtnjEdhItbfY");
            //String postJsonData = "{\"to\": \"/topics/reddit_updates\", \"data\": {\"message\": \"" + params[0] + "\"}}";
            String postJsonData1 = "{\"to\": \"/topics/twitter_updates\", \"data\": {\"message\": \""
                    +message+" ~"+title+"~"+alert_identifier+"\"}}";
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postJsonData1);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);
                /*JSONObject parent = new JSONObject();

                JSONObject msg = new JSONObject();
                msg.put("message", "test8");


                parent.put("to", "/topics/updates");
                parent.put("data", msg);*/


            System.out.println("\nSending 'POST' request to URL : " + url);
            // System.out.println("Post parameters : " + parent.toString());
            System.out.println("Response Code : " + responseCode + " " + con.getResponseMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}