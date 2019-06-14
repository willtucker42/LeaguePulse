package us.leagepulse.leaguepulse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.leagepulse.leaguepulse.R;

import us.leagepulse.leaguepulse.data.RecyclerItem;
import us.leagepulse.leaguepulse.data.RedditPHP;
import us.leagepulse.leaguepulse.data.redditdata.datamodel.Post;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseMessaging.getInstance().subscribeToTopic("reddit_updates");
        FirebaseMessaging.getInstance().subscribeToTopic("twitter_updates");
        ArrayList<RecyclerItem> recyclerItemArrayList;
        GetRedditData getRedditData = new GetRedditData();
        try {
            recyclerItemArrayList = getRedditData.execute().get();
            System.out.println("Finished");
            saveData(recyclerItemArrayList);
            final Intent i = new Intent(SplashActivity.this, MainActivity.class);
         //   i.putExtra("data", arrayLists);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(i);
                }
            }, 2500);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //getRedditData.execute();
    }
    private void saveData(ArrayList<RecyclerItem> recyclerItemArrayList){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(recyclerItemArrayList);
        editor.putString("list", json);
        editor.apply();
    }
    private class GetRedditData extends AsyncTask<String, Void, ArrayList<RecyclerItem>> {

        private static final String TAG = "GetRedditDataAsyncTask";

        @Override
        protected ArrayList<RecyclerItem> doInBackground(String... strings) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://leaguepulsereddit-env.pggnwbfn7t.us-west-1.elasticbeanstalk.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RedditPHP redditPHP = retrofit.create(RedditPHP.class);

            Call<List<Post>> call = redditPHP.getData();
            Response<List<Post>> response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                Log.e(TAG, "Catch: " + e.toString());
            }
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            final DecimalFormat d_format = new DecimalFormat("#.#");
            final ArrayList<RecyclerItem> recyclerItemArrayList = new ArrayList<>();
            assert response != null;
            if (!response.isSuccessful()) {
                //not successful
                Log.e(TAG, String.valueOf(response.code()));
                return null;
            }
            List<Post> posts = response.body();
            assert posts != null;
            for (Post post : posts) {
                String self_text = post.getSelf_text();
                Date date = new Date(post.getCreated_utc() * 1000L);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America"));
                if (self_text.equals("")) {
                    recyclerItemArrayList.add(new RecyclerItem(post.getTitle(),post.getUrl(),
                            simpleDateFormat.format(date),String.valueOf(d_format.format(post.getTrending_level())),
                            "yes",post.getPermalink(),post.getTwitter_handle(),
                            post.getTwitter_name(),post.getTwitter_media_url(),post.getTwitter_media_type()));
                } else {
                    recyclerItemArrayList.add(new RecyclerItem(post.getTitle(),self_text,
                            simpleDateFormat.format(date), String.valueOf(d_format.format(post.getTrending_level())),
                            "no",post.getPermalink(),post.getTwitter_handle(),
                            post.getTwitter_name(),post.getTwitter_media_url(),post.getTwitter_media_type()));
                }
                //System.out.println("Post text: " + post.getSelf_text());
            }
            return recyclerItemArrayList;
        }

    }
}
