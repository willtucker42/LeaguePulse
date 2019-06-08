package com.example.leaguepulse;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private ArrayList<String> link_to_reddit = new ArrayList<>();
    private ArrayList<String> post_titles = new ArrayList<>();
    private ArrayList<String> self_text = new ArrayList<>();
    private ArrayList<String> authors = new ArrayList<>();
    private ArrayList<String> clickable_links = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> trending_levels = new ArrayList<>();
    RecyclerViewAdapter adapter;
    ReturnData returnData = new ReturnData();

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeVariables(root_view);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void initializeVariables(View root_view) {
        Toolbar toolbar = root_view.findViewById(R.id.home_toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("My title");
        initRecyclerLists(root_view);
    }

    private void initRecyclerLists(View root_view) {
        post_titles.addAll(returnData.getRedditTrendingLists().get(0));
        self_text.addAll(returnData.getRedditTrendingLists().get(1));
        authors.addAll(returnData.getRedditTrendingLists().get(2));
        clickable_links.addAll(returnData.getRedditTrendingLists().get(3));
        link_to_reddit.addAll(returnData.getRedditTrendingLists().get(4));
        dates.addAll(returnData.getRedditTrendingLists().get(5));
        trending_levels.addAll(returnData.getRedditTrendingLists().get(6));
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
}
