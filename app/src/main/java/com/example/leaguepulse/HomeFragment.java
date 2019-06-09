package com.example.leaguepulse;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leaguepulse.data.ReturnData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private ArrayList<String> link_to_reddit = new ArrayList<>();
    private ArrayList<String> post_titles = new ArrayList<>();
    private ArrayList<String> self_text = new ArrayList<>();
    private ArrayList<String> authors = new ArrayList<>();
    private ArrayList<String> clickable_links = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> trending_levels = new ArrayList<>();
    ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
    RecyclerViewAdapter adapter;
    ReturnData returnData = new ReturnData();
    RecyclerView recyclerView;
    View root_view;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_home, container, false);
        loadData();
        /*try {
            if (returnData.getRedditTrendingLists().get(0) != null) {
                post_titles.addAll(returnData.getRedditTrendingLists().get(0));
                self_text.addAll(returnData.getRedditTrendingLists().get(1));
                authors.addAll(returnData.getRedditTrendingLists().get(2));
                clickable_links.addAll(returnData.getRedditTrendingLists().get(3));
                link_to_reddit.addAll(returnData.getRedditTrendingLists().get(4));
                dates.addAll(returnData.getRedditTrendingLists().get(5));
                trending_levels.addAll(returnData.getRedditTrendingLists().get(6));
            }else{
                Log.e(TAG, "There was an issue getting data");
            }
        }catch (Exception e){
            Log.e(TAG,"Catch1: "+e.toString());
        }*/
        post_titles.addAll(arrayLists.get(0));
        self_text.addAll(arrayLists.get(1));
        authors.addAll(arrayLists.get(2));
        clickable_links.addAll(arrayLists.get(3));
        link_to_reddit.addAll(arrayLists.get(4));
        dates.addAll(arrayLists.get(5));
        trending_levels.addAll(arrayLists.get(6));

        BindRecyclerData bindRecyclerData = new BindRecyclerData();
        bindRecyclerData.execute(root_view);

        System.out.println("Setting adapter");
        initializeVariables(root_view);
        return root_view;
    }

    private void loadData() {
        System.out.println("Loading data...");
        SharedPreferences sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<ArrayList<String>>>() {
        }.getType();
        arrayLists = gson.fromJson(json, type);
        if (arrayLists != null) {
            System.out.println("The list size is " + arrayLists.size());
        } else {
            Log.e(TAG, "Array list is null");
        }
    }

    private void initializeVariables(View root_view) {
        Toolbar toolbar = root_view.findViewById(R.id.home_toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("My title");
        //initRecyclerLists(root_view);
    }

    private void initRecyclerLists(View root_view) {
        initRecyclerView(root_view);
    }

    private void initRecyclerView(View root_view) {
        Log.d(TAG, "initRecyclerView");

        RecyclerView recyclerView = root_view.findViewById(R.id.home_recyclerview);
        recyclerView.setAdapter(adapter);
        adapter = new RecyclerViewAdapter(post_titles, self_text, authors,
                clickable_links, getActivity(), link_to_reddit, dates, trending_levels);
        System.out.println("Setting adapter");
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private class BindRecyclerData extends AsyncTask<View, Void, RecyclerViewAdapter> {

        @Override
        protected RecyclerViewAdapter doInBackground(View... views) {
            View root_view = views[0];
            recyclerView = root_view.findViewById(R.id.home_recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new RecyclerViewAdapter(post_titles, self_text, authors,
                    clickable_links, getActivity(), link_to_reddit, dates, trending_levels);
            return adapter;
        }

        @Override
        protected void onPostExecute(RecyclerViewAdapter adapter) {
            System.out.println("Finished binding data");
            recyclerView.setAdapter(adapter);
        }
    }
}
