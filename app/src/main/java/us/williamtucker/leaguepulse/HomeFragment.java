package us.williamtucker.leaguepulse;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leagepulse.leaguepulse.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import us.williamtucker.leaguepulse.data.RecyclerItem;
import us.williamtucker.leaguepulse.data.RedditPHP;
import us.williamtucker.leaguepulse.data.RedditTwitterPHP;
import us.williamtucker.leaguepulse.data.TwitterPHP;
import us.williamtucker.leaguepulse.data.redditdata.datamodel.Post;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "HomeFragment";
    private ArrayList<RecyclerItem> recyclerItems = new ArrayList<>();
    private ArrayList<RecyclerItem> refreshList = new ArrayList<>();
    private ArrayList<RecyclerItem> recyclerItems_copy = new ArrayList<>();
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private final int FILTER_BUTTON_TAP = 0;
    private final int REDDIT_BUTTON_TAP = 1;
    private final int TWITTER_BUTTON_TAP = 2;
    private final int SHARE_BUTTON_TAP = 3;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_home, container, false);
        SharedPreferences sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences("shared preferences", MODE_PRIVATE);
        System.out.println("Current thread: " + Thread.currentThread());
        loadData(sharedPreferences);
        recyclerView = root_view.findViewById(R.id.home_recyclerview);
        initializeVariables(root_view, sharedPreferences);
        BindRecyclerData bindRecyclerData = new BindRecyclerData();
        bindRecyclerData.execute(root_view);
        return root_view;
    }

    private void sendEngagementData(final int type) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("LeaguePulseEngagement");

        // Retrieve the object by id
        query.getInBackground("3jLHQQIAh1", new GetCallback<ParseObject>() {
            public void done(ParseObject engagement, ParseException e) {
                if (e == null) {
                    // Now let's update it with some new data. In this case, only cheatMode and score
                    switch (type) {
                        case FILTER_BUTTON_TAP:
                            engagement.increment("filter_button_taps");
                            break;
                        case REDDIT_BUTTON_TAP:
                            engagement.increment("reddit_button_taps");
                            break;
                        case TWITTER_BUTTON_TAP:
                            engagement.increment("twitter_button_taps");
                            break;
                        case SHARE_BUTTON_TAP:
                            engagement.increment("share_button_taps");
                            break;
                        default:
                            Log.e(TAG, "sendEngagementData Error. int type: " + type);
                    }
                    engagement.saveInBackground();
                } else {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    private void loadData(SharedPreferences sharedPreferences) {
        System.out.println("Loading data...");

        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<RecyclerItem>>() {
        }.getType();
        recyclerItems = gson.fromJson(json, type);
        if (recyclerItems != null) {
            System.out.println("The list size is " + recyclerItems.size());
        } else {
            Log.e(TAG, "Array list is null");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        System.out.println("\n\n In oncreateOptionsMenu");
        MenuInflater inflater1 = Objects.requireNonNull(getActivity()).getMenuInflater();
        inflater1.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initializeVariables(final View root_view, final SharedPreferences sharedPreferences) {
        CoordinatorLayout fragment_home_layout = root_view.findViewById(R.id.fragment_home_layout);

        if (sharedPreferences.getBoolean("night_light_enabled", false)) {
            fragment_home_layout.setBackgroundColor(Color.parseColor("#000000"));
        } else {
            fragment_home_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        String[] spinner_array = new String[]{"Reddit & Twitter", "Reddit Only", "Twitter Only"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item, spinner_array);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        Toolbar toolbar = root_view.findViewById(R.id.home_toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("Trending");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        refreshLayout = root_view.findViewById(R.id.swipe_container);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        refreshLayout.setOnRefreshListener(this);
        Button filter_button = root_view.findViewById(R.id.filter_button);
        filter_button.bringToFront();
        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Button thread: " + Thread.currentThread());
                sendEngagementData(FILTER_BUTTON_TAP);
                //refresh();
                recyclerItems.clear();
                loadData(sharedPreferences);
                openFilterDialogue();
            }
        });
        //initRecyclerLists(root_view);
    }

    @SuppressLint("InflateParams")
    private void openFilterDialogue() {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        View alertView;
        alertView = Objects.requireNonNull(getActivity()).getLayoutInflater()
                .inflate(R.layout.filter_layout, null);
        builder.setTitle("Filter");
        final AppCompatEditText search_edit_text = alertView.findViewById(R.id.search_edit_text);
        final AppCompatCheckBox twitter_check_box = alertView.findViewById(R.id.twitter_check_box);
        final AppCompatCheckBox reddit_check_box = alertView.findViewById(R.id.reddit_check_box);
        twitter_check_box.setChecked(true);
        reddit_check_box.setChecked(true);

        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean reddit_checked = reddit_check_box.isChecked();
                boolean twitter_checked = twitter_check_box.isChecked();

                if (!reddit_checked && !twitter_checked) {
                    Toast.makeText(getActivity(), "Twitter and/or Reddit must be checked", Toast.LENGTH_LONG).show();
                } else {
                    createFilteredList(Objects.requireNonNull(search_edit_text.getText()).toString().toLowerCase()
                            , twitter_checked, reddit_checked);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setView(alertView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createFilteredList(String search_query, boolean twitter_checked
            , boolean reddit_checked) {
        //recyclerItems.clear();
        ArrayList<RecyclerItem> first_list = new ArrayList<>();
        ArrayList<RecyclerItem> final_list = new ArrayList<>();
        System.out.println("Search query: " + search_query + " Twitter boolean: " + twitter_checked + " reddit boolean " + reddit_checked);
        for (RecyclerItem item : recyclerItems) {
            if (reddit_checked && twitter_checked) {
                first_list.add(item);
                System.out.println("Reddit and Twitter item added");
            } else if (twitter_checked && !item.getmTwitter_name().equals("")) {
                first_list.add(item);
                System.out.println("Twitter only item added");
            } else if (reddit_checked && item.getmTwitter_name().equals("")) {
                first_list.add(item);
                System.out.println("Reddit only item added");
            } else {
                Log.e(TAG, "Unexpected value(s) for reddit_checked, twitter_checked");
            }
        }
        if (first_list.size() > 0 && !search_query.trim().equals("")) {
            System.out.println("We're in here");
            for (RecyclerItem item : first_list) {
                if (twitter_checked && (item.getmTwitter_handle().toLowerCase().contains(search_query)
                        || item.getmTwitter_name().toLowerCase().contains(search_query)) || item.getmSelf_text().toLowerCase().contains(search_query)) {
                    final_list.add(item);
                    System.out.println(search_query + " FOUND in tweet");
                    continue;
                } else {
                    // System.out.println(search_query + " not found in tweet");
                }
                if (reddit_checked && (item.getmPost_title().toLowerCase().contains(search_query) || item.getmSelf_text().toLowerCase().contains(search_query))) {
                    final_list.add(item);
                    System.out.println(search_query + " FOUND in REDDIT POST");
                } else {
                    // System.out.println(search_query + " not found in reddit post");
                }

            }
            recyclerItems = final_list;
        } else {
            recyclerItems = first_list;
            Log.e(TAG, "Either searchquery is empty or first list is empty");
        }
        System.out.println("final_list size: " + final_list.size());
        BindRecyclerData bindRecyclerData = new BindRecyclerData();
        bindRecyclerData.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class BindRecyclerData extends AsyncTask<View, Integer, RecyclerViewAdapter> {

        @Override
        protected RecyclerViewAdapter doInBackground(View... views) {
            try {
                System.out.println("Bind recycler data doinbackground Current thread: " + Thread.currentThread());

                //refreshLayout.setRefreshing(true);
                System.out.println("here");
                adapter = new RecyclerViewAdapter(recyclerItems, getActivity(),
                        Objects.requireNonNull(getActivity()).
                                getSharedPreferences("shared preferences", MODE_PRIVATE)
                                .getBoolean("night_light_enabled", false));
                System.out.println("here2");
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return adapter;
        }

        @Override
        protected void onPostExecute(RecyclerViewAdapter adapter) {
            System.out.println("Bind recycler on post execute Current thread: " + Thread.currentThread());
            System.out.println("Finished binding data");
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(false);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    public void onRefresh() {
        System.out.println("Onrefresh thread: " + Thread.currentThread());
        GetRedditTwitterData getRedditTwitterData = new GetRedditTwitterData();
        getRedditTwitterData.execute();
    }

    public void refresh() {
        System.out.println("Refresh thread: " + Thread.currentThread());
        GetRedditTwitterData getRedditTwitterData = new GetRedditTwitterData();
        getRedditTwitterData.execute();
    }


    @SuppressLint("StaticFieldLeak")
    private class GetTwitterData extends AsyncTask<String, Void, ArrayList<RecyclerItem>> {
        @Override
        protected void onPostExecute(ArrayList<RecyclerItem> recyclerItems1) {
            recyclerItems = recyclerItems1;
            BindRecyclerData bindRecyclerData = new BindRecyclerData();
            bindRecyclerData.execute();
        }

        @Override
        protected ArrayList<RecyclerItem> doInBackground(String... strings) {
            recyclerItems.clear();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://getonlytrendingtwitter.us-west-1.elasticbeanstalk.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            TwitterPHP twitterPHP = retrofit.create(TwitterPHP.class);

            Call<List<Post>> call = twitterPHP.getData();
            Response<List<Post>> response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                Log.e(TAG, "Catch: " + e.toString());
            }
            Calendar cal = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a");
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
                    if (self_text.contains("---\n\n###MATCH 1:") && self_text.contains("postmatch.team")
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
                            //System.out.println(score.trim());
                            team2 = score.substring(score.indexOf("-") + 3).trim();
                            if (!team2.contains("G2") && score.contains("G2")) {
                                team1 = "G2 Esports";
                            } else if (!team2.contains("100") && score.contains("100")) {
                                team1 = "100 Thieves";
                            } else {
                                team1 = score.split("[0-9]")[0].trim();
                            }
                            if (winner_line_match.find()) {
                                winner_line = "Winner: " + winner_line_match.group(1).replace("*", "") + " Minutes";
                            }
                            /*System.out.println("Team1: " + team1);
                            System.out.println("Team2: " + team2);
                            System.out.println("Winner line: " + winner_line);*/
                        }

                        //System.out.println(self_text.substring(self_text.indexOf("---\n\n###")+1));
                    } else {
                        System.out.println("Doesn't");
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
                            week_region == null ? "" : week_region, getTeamLogo(team1, week_region),
                            getTeamLogo(team2, week_region)));
                }
                //System.out.println("Post text: " + post.getSelf_text());
            }
            return recyclerItemArrayList;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetRedditData extends AsyncTask<String, Void, ArrayList<RecyclerItem>> {
        @Override
        protected void onPostExecute(ArrayList<RecyclerItem> recyclerItems1) {
            recyclerItems = recyclerItems1;
            BindRecyclerData bindRecyclerData = new BindRecyclerData();
            bindRecyclerData.execute();
        }

        @Override
        protected ArrayList<RecyclerItem> doInBackground(String... strings) {

            recyclerItems.clear();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://getonlytrendingreddit.us-west-1.elasticbeanstalk.com/")
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
            Calendar cal = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a");
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
                    if (self_text.contains("---\n\n###MATCH 1:") && self_text.contains("postmatch.team")
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
                            //System.out.println(score.trim());
                            team2 = score.substring(score.indexOf("-") + 3).trim();
                            if (!team2.contains("G2") && score.contains("G2")) {
                                team1 = "G2 Esports";
                            } else if (!team2.contains("100") && score.contains("100")) {
                                team1 = "100 Thieves";
                            } else {
                                team1 = score.split("[0-9]")[0].trim();
                            }
                            if (winner_line_match.find()) {
                                winner_line = "Winner: " + winner_line_match.group(1).replace("*", "") + " Minutes";
                            }
                            /*System.out.println("Team1: " + team1);
                            System.out.println("Team2: " + team2);
                            System.out.println("Winner line: " + winner_line);*/
                        }

                        //System.out.println(self_text.substring(self_text.indexOf("---\n\n###")+1));
                    } else {
                        System.out.println("Doesn't");
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
                            week_region == null ? "" : week_region, getTeamLogo(team1, week_region),
                            getTeamLogo(team2, week_region)));
                }
                //System.out.println("Post text: " + post.getSelf_text());
            }
            return recyclerItemArrayList;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetRedditTwitterData extends AsyncTask<String, Void, ArrayList<RecyclerItem>> {

        private static final String TAG = "GetRedditDataAsyncTask";

        @Override
        protected void onPostExecute(ArrayList<RecyclerItem> recyclerItems1) {
            System.out.println("Refresh async on post execute: " + Thread.currentThread());
            recyclerItems = recyclerItems1;
            BindRecyclerData bindRecyclerData = new BindRecyclerData();
            bindRecyclerData.execute();
        }

        @Override
        protected ArrayList<RecyclerItem> doInBackground(String... strings) {
            System.out.println("Refresh async: " + Thread.currentThread());
            recyclerItems.clear();
            Log.i("Recycler item size", String.valueOf(recyclerItems.size()));
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
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a");
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
                    if (self_text.contains("---\n\n###MATCH 1:") && self_text.contains("postmatch.team")
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
                            //System.out.println(score.trim());
                            team2 = score.substring(score.indexOf("-") + 3).trim();
                            if (!team2.contains("G2") && score.contains("G2")) {
                                team1 = "G2 Esports";
                            } else if (!team2.contains("100") && score.contains("100")) {
                                team1 = "100 Thieves";
                            } else {
                                team1 = score.split("[0-9]")[0].trim();
                            }
                            if (winner_line_match.find()) {
                                winner_line = "Winner: " + winner_line_match.group(1).replace("*", "") + " Minutes";
                            }
                            /*System.out.println("Team1: " + team1);
                            System.out.println("Team2: " + team2);
                            System.out.println("Winner line: " + winner_line);*/
                        }

                        //System.out.println(self_text.substring(self_text.indexOf("---\n\n###")+1));
                    } else {
                        //System.out.println("Doesn't");
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
                            week_region == null ? "" : week_region, getTeamLogo(team1, week_region),
                            getTeamLogo(team2, week_region)));
                }
                //System.out.println("Post text: " + post.getSelf_text());
            }
            return recyclerItemArrayList;
        }

    }

    private int getTeamLogo(String team1, String week_region) {
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
            if (team1.equals("Cloud9")) {
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
