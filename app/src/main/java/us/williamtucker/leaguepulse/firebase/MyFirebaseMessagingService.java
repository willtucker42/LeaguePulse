package us.williamtucker.leaguepulse.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.leagepulse.leaguepulse.R;

import us.williamtucker.leaguepulse.SplashActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]


        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        String body, title;

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        String reddit_alert_option = sharedPreferences.getString("reddit_alert_option", "top");
        String twitter_alert_option = sharedPreferences.getString("twitter_alert_option", "top");
        boolean receive_twitter_alerts = sharedPreferences.getBoolean("receive_twitter_alerts", true);
        boolean receive_reddit_alerts = sharedPreferences.getBoolean("receive_reddit_alerts", true);
        boolean receive_match_alerts = sharedPreferences.getBoolean("receive_match_alerts", true);
        boolean na_region_checked = sharedPreferences.getBoolean("na_region_checked", true);
        boolean eu_region_checked = sharedPreferences.getBoolean("eu_region_checked", true);

        // Check if message contains a data payload.
        String data = remoteMessage.getData().toString();
        Log.e(TAG, "Message data payload: " + data);
        body = data;
        body = body.substring(body.indexOf("~") + 1);
        body = body.substring(0, body.indexOf("~"));

        title = data;
        title = title.substring(title.indexOf("=") + 1);
        title = title.substring(0, title.indexOf("~"));
        if (remoteMessage.getData().size() > 0) {
            if (data.contains("type:twitter") && receive_twitter_alerts) {
                if (data.contains("region:eu") && eu_region_checked) {
                    if (data.contains("trending:top")) {
                        System.out.println("notification twitter, eu, top");
                        //if the alert identifies it as top trending, then we send it no matter what. Because either the user wants to get all the messages (top included) or top messages
                        sendNotification(title, body);
                    } else {
                        assert twitter_alert_option != null;
                        if (data.contains("trending:all") && twitter_alert_option.equals("all")) {
                            System.out.println("notification twitter, eu, all");
                            sendNotification(title, body);
                        } else {
                            Log.e(TAG, "Error1: ");
                        }
                    }
                } else if (data.contains("region:na") && na_region_checked) {
                    if (data.contains("trending:top")) {
                        System.out.println("notification twitter, na, top");
                        sendNotification(title, body);
                    } else {
                        assert twitter_alert_option != null;
                        if (data.contains("trending:all") && twitter_alert_option.equals("all")) {
                            System.out.println("notification twitter, na, all");
                            sendNotification(title, body);
                        } else {
                            Log.e(TAG, "Error1: ");
                        }
                    }

                } else if (data.contains("region: al")) {
                    if (data.contains("trending:top")) {
                        System.out.println("notification twitter, al, top");
                        sendNotification(title, body);
                    } else {
                        assert twitter_alert_option != null;
                        if (data.contains("trending:all") && twitter_alert_option.equals("all")) {
                            System.out.println("notification twitter, al, all");
                            sendNotification(title, body);
                        } else {
                            Log.e(TAG, "Error1: ");
                        }
                    }
                }
            } else if (data.contains("type:reddit") && receive_reddit_alerts) {
                if (data.contains("trending:top")) {
                    System.out.println("notification reddit, top");
                    sendNotification(title, body);
                } else {
                    assert reddit_alert_option != null;
                    if (data.contains("trending:all") && reddit_alert_option.equals("all")) {
                        System.out.println("notification reddit, all");
                        sendNotification(title, body);
                    }
                }
                //this is a reddit notification
            } else if (data.contains("type:match") && receive_match_alerts) {
                sendNotification(title, body);
                //this is a match results notification
            } else {
                Log.e(TAG, "There was an error identifying the notification type");
            }
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
        }
        if (remoteMessage.getData().size() > 0) {


            //sendNotification(title, body);
            //sendNotification("League Pulse", remoteMessage.getData().toString());


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */

    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        //  Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.transparentlp_launcher)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}