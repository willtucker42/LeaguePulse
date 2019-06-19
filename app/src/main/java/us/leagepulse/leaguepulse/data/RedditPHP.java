package us.leagepulse.leaguepulse.data;

import us.leagepulse.leaguepulse.data.redditdata.datamodel.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RedditPHP {
    //@GET("get-trendingredditdata-json1.php")
    @GET("get-trendingtwitterreddit-json.php")
    Call<List<Post>> getData();
}
