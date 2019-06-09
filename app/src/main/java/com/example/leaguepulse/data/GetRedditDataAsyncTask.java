package com.example.leaguepulse.data;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.leaguepulse.MainActivity;
import com.example.leaguepulse.data.redditdata.datamodel.Post;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetRedditDataAsyncTask extends AsyncTask<String, Void, ArrayList<ArrayList<String>>> {

    private static final String TAG = "GetRedditDataAsyncTask";

    @Override
    protected ArrayList<ArrayList<String>> doInBackground(String... strings) {
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
            Log.e(TAG, "Catch: "+ e.toString());
        }
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.US);
        final DecimalFormat d_format = new DecimalFormat("#.#");
        ArrayList<ArrayList<String>> listOfRedditPostLists = new ArrayList<>();
        final ArrayList<String> post_titles = new ArrayList<>();
        final ArrayList<String> self_texts = new ArrayList<>();
        final ArrayList<String> authors = new ArrayList<>();
        final ArrayList<String> clickable_link = new ArrayList<>();
        final ArrayList<String> link_to_reddit = new ArrayList<>();
        final ArrayList<String> dates = new ArrayList<>();
        final ArrayList<String> trending_levels = new ArrayList<>();
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
            self_texts.add(self_text);

            if (self_text.equals("")) {
                self_texts.add(post.getUrl()); //adds url of link to article/video if there is no selftext
                clickable_link.add("yes");
            } else {
                self_texts.add(self_text);
                clickable_link.add("no");
            }
            System.out.println("Post title: " + post.getTitle());
            post_titles.add(post.getTitle());
            authors.add(post.getAuthor());
            clickable_link.add(post.getTitle());
            Date date = new Date(post.getCreated_utc() * 1000L);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America"));
            dates.add(simpleDateFormat.format(date));
            link_to_reddit.add(post.getPermalink());
            trending_levels.add(String.valueOf(d_format.format(post.getTrending_level())));
        }
        listOfRedditPostLists.add(post_titles);
        listOfRedditPostLists.add(self_texts);
        listOfRedditPostLists.add(authors);
        listOfRedditPostLists.add(clickable_link);
        listOfRedditPostLists.add(link_to_reddit);
        listOfRedditPostLists.add(dates);
        listOfRedditPostLists.add(trending_levels);
        return listOfRedditPostLists;
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList<String>> arrayLists) {
        Intent i = new Intent();
       // i.putExtra("data", result);
       // startActivity(i);
       // finish();
        super.onPostExecute(arrayLists);
    }
}
