package us.leagepulse.leaguepulse.data;

public class RecyclerItem {
    private String mPost_title;
    private String mSelf_text;
    private String mDate;
    private String mTrending_level;
    private String mClickable_link;
    private String mLink_to_reddit;

    public RecyclerItem(String post_title, String self_text, String date, String trending_level,
                        String clickable_link, String link_to_reddit){
        mPost_title = post_title;
        mSelf_text = self_text;
        mDate = date;
        mTrending_level = trending_level;
        mClickable_link = clickable_link;
        mLink_to_reddit = link_to_reddit;
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
