package com.redditgetter.DataModel.children;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {


    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("selftext")
    @Expose
    private String selftext;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("permalink")
    @Expose
    private String permalink;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("created_utc")
    @Expose
    private long created_utc;

    @SerializedName("contest_mode")
    @Expose
    private String contest_mode;

    @SerializedName("subreddit")
    @Expose
    private String subreddit;

    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("score")
    @Expose
    private int score;

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreated_utc() {
        return created_utc;
    }

    public void setCreated_utc(long created_utc) {
        this.created_utc = created_utc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContest_mode() {
        return contest_mode;
    }

    public void setContest_mode(String contest_mode) {
        this.contest_mode = contest_mode;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSelftext() {
        return selftext;
    }

    public void setSelftext(String selftext) {
        this.selftext = selftext;
    }

    @Override
    public String toString() {
        return "Data{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", permalink='" + permalink + '\'' +
                ", url='" + url + '\'' +
                ", created_utc=" + created_utc +
                ", contest_mode='" + contest_mode + '\'' +
                ", subreddit='" + subreddit + '\'' +
                ", author='" + author + '\'' +
                ", score=" + score +
                '}';
    }
}
