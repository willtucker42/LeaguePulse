package us.williamtucker.leaguepulse.data;

public class RecyclerItem {
    private String mPost_title;
    private String mSelf_text;
    private String mDate;
    private String mTrending_level;
    private String mClickable_link;
    private String mPermalink;
    private String mTwitter_handle;
    private String mTwitter_name;
    private String mTwitter_media_url;
    private String mMedia_type;
    private String mUser_profile_pic_url;
    private String mTeam1;
    private String mTeam2;
    private String mWinner_line;
    private String mWeek_region;
    private int mTeam1_logo;
    private int mTeam2_logo;

    public RecyclerItem(String post_title, String self_text, String date, String trending_level,
                        String clickable_link, String permalink, String twitter_handle,
                        String twitter_name, String twitter_media_url, String media_type,
                        String user_profile_pic_url, String team1, String team2, String winner_line,
                        String week_region, int team1_logo,int team2_logo){
        mPost_title = post_title;
        mSelf_text = self_text;
        mDate = date;
        mTrending_level = trending_level;
        mClickable_link = clickable_link;
        mPermalink= permalink;
        mTwitter_handle = twitter_handle;
        mTwitter_name = twitter_name;
        mTwitter_media_url = twitter_media_url;
        mMedia_type = media_type;
        mUser_profile_pic_url = user_profile_pic_url;
        mTeam1 = team1;
        mTeam2 = team2;
        mWinner_line = winner_line;
        mWeek_region = week_region;
        mTeam1_logo = team1_logo;
        mTeam2_logo = team2_logo;
    }
    public int getmTeam1_logo() {
        return mTeam1_logo;
    }

    public int getmTeam2_logo() {
        return mTeam2_logo;
    }

    public String getmWeek_region() {
        return mWeek_region;
    }

    public String getmTeam1() {
        return mTeam1;
    }

    public String getmTeam2() {
        return mTeam2;
    }

    public String getmWinner_line() {
        return mWinner_line;
    }

    public String getmUser_profile_pic_url() {
        return mUser_profile_pic_url;
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

    public String getmPermalink() {
        return mPermalink;
    }
}
