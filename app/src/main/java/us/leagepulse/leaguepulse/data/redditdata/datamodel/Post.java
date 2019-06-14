package us.leagepulse.leaguepulse.data.redditdata.datamodel;

public class Post {
    private String id;
    private String author;
    private long created_utc;
    private long current_utc;
    private String day_of_week;
    private int hour_of_day;
    private int minutes_ago_utc;
    private String permalink;
    private int score;
    private double score_per_minute;
    private String self_text;
    private String subreddit;
    private String title;
    private double trending_level;
    private String url;
    private String twitter_handle;
    private String twitter_name;
    private String twitter_media_url;
    private String twitter_media_type;
    private long twitter_id;


    public String getTwitter_handle() {
        return twitter_handle;
    }

    public String getTwitter_name() {
        return twitter_name;
    }

    public String getTwitter_media_url() {
        return twitter_media_url;
    }

    public String getTwitter_media_type() {
        return twitter_media_type;
    }

    public long getTwitter_id() {
        return twitter_id;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public long getCreated_utc() {
        return created_utc;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getSelf_text() {
        return self_text;
    }

    public String getTitle() {
        return title;
    }

    public double getTrending_level() {
        return trending_level;
    }

    public String getUrl() {
        return url;
    }
}
