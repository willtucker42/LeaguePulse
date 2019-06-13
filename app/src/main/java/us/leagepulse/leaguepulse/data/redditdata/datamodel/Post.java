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

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public long getCreated_utc() {
        return created_utc;
    }

    public long getCurrent_utc() {
        return current_utc;
    }

    public String getDay_of_week() {
        return day_of_week;
    }

    public int getHour_of_day() {
        return hour_of_day;
    }

    public int getMinutes_ago_utc() {
        return minutes_ago_utc;
    }

    public String getPermalink() {
        return permalink;
    }

    public int getScore() {
        return score;
    }

    public double getScore_per_minute() {
        return score_per_minute;
    }

    public String getSelf_text() {
        return self_text;
    }

    public String getSubreddit() {
        return subreddit;
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
