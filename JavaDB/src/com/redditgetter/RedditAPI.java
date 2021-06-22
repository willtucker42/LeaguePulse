package com.redditgetter;

import com.redditgetter.DataModel.Feed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface RedditAPI {
        String BASE_URL = "https://www.reddit.com/";

        @Headers("Content-Type: application/json")
        @GET(".json")
        Call<Feed> getData();

}
