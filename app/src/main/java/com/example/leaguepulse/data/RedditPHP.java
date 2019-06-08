package com.example.leaguepulse.data;

import com.example.leaguepulse.data.redditdata.datamodel.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RedditPHP {
    @GET("get-trendingredditdata-json1.php")
    Call<List<Post>> getData();
}
