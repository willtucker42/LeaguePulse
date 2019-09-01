package us.williamtucker.leaguepulse.data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import us.williamtucker.leaguepulse.data.redditdata.datamodel.Post;

public interface RedditPHP {
    @GET("get-onlytrendingreddit-json.php")
    Call<List<Post>> getData();
}
