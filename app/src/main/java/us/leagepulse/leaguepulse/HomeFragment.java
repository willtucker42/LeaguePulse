package us.leagepulse.leaguepulse;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leagepulse.leaguepulse.R;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import us.leagepulse.leaguepulse.data.RecyclerItem;
import us.leagepulse.leaguepulse.data.RedditPHP;
import us.leagepulse.leaguepulse.data.redditdata.datamodel.Post;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "HomeFragment";
    private ArrayList<RecyclerItem> recyclerItems = new ArrayList<>();
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    View root_view;
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_home, container, false);
        loadData();
        recyclerView = root_view.findViewById(R.id.home_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initializeVariables(root_view);
        BindRecyclerData bindRecyclerData = new BindRecyclerData();
        bindRecyclerData.execute(root_view);

        return root_view;
    }

    private void loadData() {
        System.out.println("Loading data...");
        SharedPreferences sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences("shared preferences", MODE_PRIVATE);
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

    private void initializeVariables(final View root_view) {
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
        //initRecyclerLists(root_view);
    }

    @Override
    public void onRefresh() {
        GetRedditData getRedditData = new GetRedditData();
        try {
            recyclerItems.clear();
            recyclerItems = getRedditData.execute().get();
            BindRecyclerData bindRecyclerData = new BindRecyclerData();
            bindRecyclerData.execute();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class BindRecyclerData extends AsyncTask<View, Integer, RecyclerViewAdapter> {

        @Override
        protected RecyclerViewAdapter doInBackground(View... views) {
            refreshLayout.setRefreshing(true);
            adapter = new RecyclerViewAdapter(recyclerItems, getActivity());
            return adapter;
        }

        @Override
        protected void onPostExecute(RecyclerViewAdapter adapter) {
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


    private void initRecyclerLists(View root_view) {
        //pretty sure I dont need these: 6/8/19 delete if 2 weeks after this date
        initRecyclerView(root_view);
    }

    private void initRecyclerView(View root_view) {
        //pretty sure I dont need these: 6/8/19 delete if 2 weeks after this date
        Log.d(TAG, "initRecyclerView");

        RecyclerView recyclerView = root_view.findViewById(R.id.home_recyclerview);
        recyclerView.setAdapter(adapter);
        adapter = new RecyclerViewAdapter(recyclerItems, getActivity());
        System.out.println("Setting adapter");
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
    private class GetRedditData extends AsyncTask<String, Void, ArrayList<RecyclerItem>> {

        private static final String TAG = "GetRedditDataAsyncTask";

        @Override
        protected ArrayList<RecyclerItem> doInBackground(String... strings) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://gettrendingreddittwitter.us-west-1.elasticbeanstalk.com/")
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
            System.out.println("Posts size: " + posts.size());
            for (Post post : posts) {
                String self_text = post.getSelf_text();
                Date date = new Date(post.getCreated_utc() * 1000L);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America"));
                String media_type = post.getMedia_type();

                String profile_pic_url =  post.getUser_profile_pic_url();
                if(profile_pic_url!=null){
                    profile_pic_url = profile_pic_url.replace("400x400","200x200");
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
                            media_type, profile_pic_url));
                } else {
                    recyclerItemArrayList.add(new RecyclerItem(post.getTitle(), self_text,
                            simpleDateFormat.format(date),
                            String.valueOf(d_format.format(post.getTrending_level())),
                            "no", post.getPermalink(),
                            "@" + post.getTwitter_handle(), post.getTwitter_name(),
                            post.getTwitter_media_url() == null ? "" : post.getTwitter_media_url(),
                            post.getMedia_type() == null ? "" : post.getMedia_type(), profile_pic_url));
                }
                //System.out.println("Post text: " + post.getSelf_text());
            }
            return recyclerItemArrayList;
        }

    }

}
