package us.williamtucker.leaguepulse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.leagepulse.leaguepulse.R;

import us.williamtucker.leaguepulse.data.RecyclerItem;
import us.williamtucker.leaguepulse.data.RedditTwitterPHP;
import us.williamtucker.leaguepulse.data.redditdata.datamodel.Post;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {
    ArrayList<RecyclerItem> recyclerItemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseMessaging.getInstance().subscribeToTopic("reddit_updates");
        FirebaseMessaging.getInstance().subscribeToTopic("twitter_updates");
        loadData();
        GetRedditData getRedditData = new GetRedditData();
        getRedditData.execute();
            /*recyclerItemArrayList = getRedditData.execute().get();
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
            }, 2500);*/
        //getRedditData.execute();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getString("twitter_alert_option", null) == null) {
            System.out.println("its adding...");
            editor.putString("twitter_alert_option", "all");
            editor.putString("reddit_alert_option", "all");
            editor.putBoolean("receive_twitter_alerts", true);
            editor.putBoolean("receive_reddit_alerts", true);
            editor.putBoolean("receive_match_alerts", true);
            editor.putBoolean("na_region_checked", true);
            editor.putBoolean("eu_region_checked", true);
            editor.apply();
        } else {
            System.out.println("Not null?!: ");
            System.out.println(sharedPreferences.getString("twitter_alert_option", ""));
            System.out.println(sharedPreferences.getString("reddit_alert_option", ""));
            System.out.println(sharedPreferences.getBoolean("receive_twitter_alerts", true));
            System.out.println(sharedPreferences.getBoolean("receive_reddit_alerts", true));
            System.out.println(sharedPreferences.getBoolean("receive_match_alerts", true));
            System.out.println(sharedPreferences.getBoolean("na_region_checked", true));
            System.out.println(sharedPreferences.getBoolean("eu_region_checked", true));
        }
    }

    private void saveData(ArrayList<RecyclerItem> recyclerItemArrayList) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(recyclerItemArrayList);
        editor.putString("list", json);
        editor.apply();
    }

    private class GetRedditData extends AsyncTask<String, Void, ArrayList<RecyclerItem>> {
        @Override
        protected void onPostExecute(ArrayList<RecyclerItem> recyclerItems) {
            System.out.println("Finished");
            recyclerItemArrayList = recyclerItems;
            //saveData(recyclerItemArrayList);
            final Intent i = new Intent(SplashActivity.this, MainActivity.class);
            //   i.putExtra("data", arrayLists);
            /*Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(i);
                }
            }, 1500);*/
            startActivity(i);
        }

        private static final String TAG = "GetRedditDataAsyncTask";

        @Override
        protected ArrayList<RecyclerItem> doInBackground(String... strings) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://gettrendingreddittwitter.us-west-1.elasticbeanstalk.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RedditTwitterPHP redditTwitterPHP = retrofit.create(RedditTwitterPHP.class);

            Call<List<Post>> call = redditTwitterPHP.getData();
            Response<List<Post>> response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                Log.e(TAG, "Catch: " + e.toString());
            }
            Calendar cal = Calendar.getInstance();
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a");
            simpleDateFormat.setTimeZone(cal.getTimeZone());
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
            System.out.println("Posts size: " + posts.size());
            for (Post post : posts) {
                String self_text = post.getSelf_text();
                Date date = new Date(post.getCreated_utc() * 1000L);
                String media_type = post.getMedia_type();
                String score, team1 = null, team2 = null, winner_line = null,
                        title = post.getTitle(), week_region = null;

                String profile_pic_url = post.getUser_profile_pic_url();
                if (profile_pic_url != null) {
                    profile_pic_url = profile_pic_url.replace("400x400", "200x200");
                }
                if (media_type == null) {
                    media_type = "";
                }
                if (self_text.equals("")) {
                    recyclerItemArrayList.add(new RecyclerItem(post.getTitle(), post.getUrl(),
                            simpleDateFormat.format(date),
                            String.valueOf(d_format.format(post.getTrending_level())),
                            "yes", post.getPermalink(),
                            "@" + post.getTwitter_handle(), post.getTwitter_name(),
                            post.getTwitter_media_url() == null ? "" : post.getTwitter_media_url(),
                            media_type, profile_pic_url, "", "", "",
                            "", 0, 0));
                } else {
                    if ((self_text.contains("---\n\n###MATCH") || self_text.contains("---\n\n### MATCH"))
                            && self_text.contains("postmatch.team")
                            && (title.contains("LEC") || title.contains("LCS"))) {
                        Matcher matcher = Pattern.compile("---\n\n###(.*?)\n").matcher(self_text);
                        Matcher winner_line_match = Pattern.compile("Winner:(.*?)m]").matcher(self_text);
                        Matcher matcher2 = Pattern.compile("/ (.*?) /").matcher(title);
                        if (matcher2.find()) {
                            week_region = matcher2.group(1);
                        }
                        if (matcher.find()) {
                            score = matcher.group().substring(8);
                            if (score.contains("(")) {
                                score = score.substring(0, score.indexOf("(") + 1);
                            }
                            score = score.replaceAll("[()\\[\\]{}]", "");
                            //score_line = score;
                            System.out.println(score.trim());
                            team2 = score.substring(score.indexOf("-") + 3).trim();
                            if (!team2.contains("G2") && score.contains("G2")) {
                                team1 = "G2 Esports";
                            } else if (!team2.contains("100") && score.contains("100")) {
                                team1 = "100 Thieves";
                            } else if (!team2.contains("9") && score.contains("9")) {
                                team1 = "Cloud9";
                            } else {
                                team1 = score.split("[0-9]")[0].trim();
                            }
                            /*if (winner_line_match.find()) {
                                winner_line = "Winner: " + winner_line_match.group(1).replace("*", "") + " Minutes";
                            } else {
                                winner_line_match = Pattern.compile("Winner:(.*?)\n").matcher(self_text);
                                if (winner_line_match.find()) {
                                    String winnerline = winner_line_match.group(1).replace("*", "");
                                    winnerline = winnerline.replace("m", "");
                                    winner_line = "Winner: " + winnerline + " Minutes";

                                } else {
                                    winner_line_match = Pattern.compile("Winner:(.*?)").matcher(self_text);
                                    if (winner_line_match.find()) {

                                    }
                                }
                            }
                            winner_line = Objects.requireNonNull(winner_line).replace("  ", "");*/
                            winner_line = "Winner: " + getWinner(score.trim());
                            System.out.println("Team1: " + team1);
                            System.out.println("Team2: " + team2);
                            System.out.println("Winner line: " + winner_line);
                        }else{
                            System.out.println("\n\n!!! Score matcher did not find anything\n\n");
                        }

                        //System.out.println(self_text.substring(self_text.indexOf("---\n\n###")+1));
                    } else {
                        self_text = self_text.replace("&amp;", "&");
                        title = title.replace("&amp;", "&");
                        System.out.println(title + " Doesn't");
                    }

                    recyclerItemArrayList.add(new RecyclerItem(post.getTitle(), self_text,
                            simpleDateFormat.format(date),
                            String.valueOf(d_format.format(post.getTrending_level())),
                            "no", post.getPermalink(),
                            "@" + post.getTwitter_handle(), post.getTwitter_name(),
                            post.getTwitter_media_url() == null ? "" : post.getTwitter_media_url(),
                            post.getMedia_type() == null ? "" : post.getMedia_type(),
                            profile_pic_url, team1 == null ? "" : team1, team2 == null ? "" : team2,
                            winner_line == null ? "" : winner_line,
                            week_region == null ? "" : week_region, getTeam1Logo(team1, week_region),
                            getTeam1Logo(team2, week_region)));
                }
                //System.out.println("Post text: " + post.getSelf_text());
            }
            saveData(recyclerItemArrayList);
            return recyclerItemArrayList;
        }

    }

    private String getWinner(String score_line) {
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n Socre line: " + score_line + "\n\n");
        String winning_team = null;
        if (score_line.contains("1-0")) {
            winning_team = score_line.substring(0, score_line.indexOf("1"));
        } else if (score_line.contains("0-1")) {
            winning_team = score_line.substring(score_line.lastIndexOf("1") + 1);
        } else if (score_line.contains("3-2") || score_line.contains("3-1") || score_line.contains("3-0")){
            winning_team = score_line.substring(0, score_line.indexOf("3"));
        }else if(score_line.contains("2-3") || score_line.contains("1-3") || score_line.contains("0-3")) {
            winning_team = score_line.substring(score_line.lastIndexOf("3") + 1);
        }
        return winning_team;
    }

    private int getTeam1Logo(String team1, String week_region) {
        if (week_region != null && week_region.contains("LEC")) {
            if (team1.equals("Origen")) {
                return R.drawable.origen_logo;
            } else if (team1.equals("Fnatic")) {
                return R.drawable.fnatic_logo;
            } else if (team1.contains("G2")) {
                return R.drawable.g2_logo_resize;
            } else if (team1.equals("Splyce")) {
                return R.drawable.splyce_logo_resize;
            } else if (team1.contains("Schalke")) {
                return R.drawable.schalke_logo_resize;
            } else if (team1.equals("SK Gaming")) {
                return R.drawable.sk_gaming_logo;
            } else if (team1.equals("Rogue")) {
                return R.drawable.rogue_logo;
            } else if (team1.contains("Misfits")) {
                return R.drawable.misfits_logo_resize;
            } else if (team1.equals("Team Vitality")) {
                return R.drawable.vitality_logo_resize;
            } else if (team1.equals("Excel Esports")) {
                return R.drawable.excel_logo;
            } else {
                return 0;
            }
        } else if (week_region != null && week_region.contains("LCS")) {
            if (team1.contains("Cloud")) {
                return R.drawable.c9_logo2_resize;
            } else if (team1.equals("Counter Logic Gaming")) {
                return R.drawable.clg_logo_resize;
            } else if (team1.equals("Golden Guardians")) {
                return R.drawable.golden_guardians_logo;
            } else if (team1.equals("OpTic Gaming") || team1.equals("Optic Gaming")) {
                return R.drawable.optic_logo;
            } else if (team1.equals("Team Liquid")) {
                return R.drawable.tl_logo_2;
            } else if (team1.equals("Team SoloMid")) {
                return R.drawable.tsm_logo;
            } else if (team1.equals("Clutch Gaming")) {
                return R.drawable.cg_logo_resize;
            } else if (team1.contains("100")) {
                return R.drawable.thieves_logo;
            } else if (team1.equals("Echo Fox")) {
                return R.drawable.exho_fox_logo;
            } else if (team1.equals("FlyQuest")) {
                return R.drawable.flyquest_logo;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}
