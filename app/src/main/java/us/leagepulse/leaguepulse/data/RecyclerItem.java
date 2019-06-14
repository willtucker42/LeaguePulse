package us.leagepulse.leaguepulse.data;

import com.squareup.picasso.Picasso;

public class RecyclerItem {
    private String mPost_title;
    private String mSelf_text;
    private String mDate;
    private String mTrending_level;
    private String mClickable_link;
    private String mLink_to_reddit;
    private String mTwitter_handle;
    private String mTwitter_name;
    private String mTwitter_media_url;
    private String mMedia_type;

    public RecyclerItem(String post_title, String self_text, String date, String trending_level,
                        String clickable_link, String link_to_reddit, String twitter_handle,
                        String twitter_name, String twitter_media_url, String media_type){
        mPost_title = post_title;
        mSelf_text = self_text;
        mDate = date;
        mTrending_level = trending_level;
        mClickable_link = clickable_link;
        mLink_to_reddit = link_to_reddit;
        mTwitter_handle = twitter_handle;
        mTwitter_name = twitter_name;
        mTwitter_media_url = twitter_media_url;
        mMedia_type = media_type;
    }

    public String getmTwitter_handle() {
        return mTwitter_handle;
    }

    public String getmTwitter_name() {
        return mTwitter_name;
    }

    public String getmTwitter_media_url() {
        return mTwitter_media_url;
    }

    public String getmMedia_type() {
        return mMedia_type;
    }

    public String getmPost_title() {
        return mPost_title;
    }

    public String getmSelf_text() {
        return mSelf_text;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmTrending_level() {
        return mTrending_level;
    }

    public String getmClickable_link() {
        return mClickable_link;
    }

    public String getmLink_to_reddit() {
        return mLink_to_reddit;
    }
}
