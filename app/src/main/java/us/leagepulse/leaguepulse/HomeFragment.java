package us.leagepulse.leaguepulse;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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

import com.leagepulse.leaguepulse.R;

import us.leagepulse.leaguepulse.data.RecyclerItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private ArrayList<RecyclerItem> recyclerItems = new ArrayList<>();
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    View root_view;
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_home, container, false);
        loadData();
        recyclerView = root_view.findViewById(R.id.home_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        BindRecyclerData bindRecyclerData = new BindRecyclerData();
        initializeVariables(root_view);
        bindRecyclerData.execute(root_view);

        System.out.println("Setting adapter");
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

    private void initializeVariables(View root_view) {
        Toolbar toolbar = root_view.findViewById(R.id.home_toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("Trending");
        //initRecyclerLists(root_view);
    }
    private class BindRecyclerData extends AsyncTask<View, Integer, RecyclerViewAdapter> {

        @Override
        protected RecyclerViewAdapter doInBackground(View... views) {
            adapter = new RecyclerViewAdapter(recyclerItems, getActivity());
            return adapter;
        }

        @Override
        protected void onPostExecute(RecyclerViewAdapter adapter) {
            System.out.println("Finished binding data");
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
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

}
