package us.williamtucker.leaguepulse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.leagepulse.leaguepulse.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import de.hdodenhof.circleimageview.CircleImageView;
import us.williamtucker.leaguepulse.data.RecyclerItem;

import java.util.ArrayList;
import java.util.Objects;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private final int VIEW_TYPE_REDDIT = 0;
    private final int VIEW_TYPE_TWITTER = 1;
    private final int VIEW_TYPE_PROGAME = 2;

    private final int DIRECT_LINK_TAP = 0;
    private final int REDDIT_BUTTON_TAP = 1;
    private final int TWITTER_BUTTON_TAP = 2;
    private final int OPEN_PHOTO_TAP = 3;
    private final int SHARE_BUTTON_TAP = 4;
    /*private ArrayList<String> post_titles;
    private ArrayList<String> self_texts;
    private ArrayList<String> authors;
    private ArrayList<String> dates;
    private ArrayList<String> link_to_reddit;
    private ArrayList<String> clickable_link;
    private ArrayList<String> trending_levels;*/
    private ArrayList<RecyclerItem> mRecycleritems;
    private Context context;
    private BandwidthMeter bMeter = new DefaultBandwidthMeter();
    private TrackSelector selector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bMeter));
    private DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
    private ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
    private BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
    private boolean dark_theme_enabled;
    SharedPreferences mSharedPreferences;

    @Override
    public int getItemViewType(int position) {

        if (!mRecycleritems.get(position).getmTeam1().equals("")) {
            return VIEW_TYPE_PROGAME;
        } else if (mRecycleritems.get(position).getmTwitter_name().equals("")) {
            return VIEW_TYPE_REDDIT;
        } else {
            return VIEW_TYPE_TWITTER;
        }

       /* if (mRecycleritems.get(position).getmTwitter_name().equals("")) {
            return VIEW_TYPE_REDDIT;
        } else {
            return VIEW_TYPE_TWITTER;
        }*/
       /* if (position==0) {
            System.out.println("POSITION IS 0");
            return VIEW_TYPE_REDDIT;
        }   else {
            System.out.println("POSITION IS 1");
            return VIEW_TYPE_TWITTER;
        }*/
    }

    private void sendEngagementData(final int type) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("LeaguePulseEngagement");

        // Retrieve the object by id
        query.getInBackground("3jLHQQIAh1", new GetCallback<ParseObject>() {
            public void done(ParseObject engagement, ParseException e) {
                if (e == null) {
                    // Now let's update it with some new data. In this case, only cheatMode and score
                    switch (type) {
                        case DIRECT_LINK_TAP:
                            engagement.increment("direct_link_taps");
                            break;
                        case REDDIT_BUTTON_TAP:
                            engagement.increment("reddit_button_taps");
                            break;
                        case TWITTER_BUTTON_TAP:
                            engagement.increment("twitter_button_taps");
                            break;
                        case OPEN_PHOTO_TAP:
                            engagement.increment("open_photo_taps");
                            break;
                        case SHARE_BUTTON_TAP:
                            engagement.increment("share_button_taps");
                            break;
                        default:
                            Log.e(TAG, "sendEngagementData Error. int type: " + type);
                    }
                    engagement.saveInBackground();
                    Log.i(TAG, "Sent");
                } else {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    RecyclerViewAdapter(ArrayList<RecyclerItem> recyclerItems, Context context, Boolean dark_theme) {
        bitmapOptions.inJustDecodeBounds = true;
        mRecycleritems = recyclerItems;
        dark_theme_enabled = dark_theme;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //System.out.println("1234asd..."+mRecycleritems.get(0).getmSelf_text());
        //inflates our view
        View view;
        RecyclerView.ViewHolder view_holder;

        if (viewType == VIEW_TYPE_TWITTER) {
            // System.out.println("ree twitter");
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.twitter_cardview,
                    parent, false);
            view_holder = new TwitterViewHolder(view);
        } else if (viewType == VIEW_TYPE_REDDIT) {
            //  System.out.println("ree reddit");
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reddit_cardview,
                    parent, false);
            view_holder = new RedditViewHolder(view);
        } else {
            //  System.out.println("ree progame");
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progame_cardview,
                    parent, false);
            view_holder = new ProGameViewHolder(view);
        }
       /* if (viewType == VIEW_TYPE_REDDIT) {

        } else {

        }*/
        /*if (view_type == VIEW_TYPE_REDDIT) {
            System.out.println("123qwe");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reddit_cardview,
                    parent, false);
            return new ViewHolder(view);
        }else if (view_type == VIEW_TYPE_TWITTER){
            System.out.println("123asd");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.twitter_cardview,
                    parent, false);
            return new ViewHolder(view);
        }else{
            throw new RuntimeException("The type has to be REDDIT or TWITTER");
        }*/
        return view_holder;

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder1, int position) {
        long startTime = System.currentTimeMillis();
        final RecyclerItem currentItem = mRecycleritems.get(position);
        //System.out.println("re onbindviewholder");
        try {
            if (viewHolder1.getItemViewType() == VIEW_TYPE_REDDIT) {
                RedditViewHolder viewHolder = (RedditViewHolder) viewHolder1;
                if (dark_theme_enabled) {
                    viewHolder.parent_layout.setBackgroundColor(Color.parseColor("#252525"));
                    viewHolder.post_title.setTextColor(Color.parseColor("#FFFFFF"));
                    viewHolder.self_text.setTextColor(Color.parseColor("#ACACAC"));
                    viewHolder.date_text.setTextColor(Color.parseColor("#ACACAC"));
                    System.out.println("Dark theme enabled for RedditCardView");
                } else {
                    viewHolder.parent_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    viewHolder.post_title.setTextColor(Color.parseColor("#000000"));
                    viewHolder.self_text.setTextColor(Color.parseColor("#515050"));
                    viewHolder.date_text.setTextColor(Color.parseColor("#515050"));
                }
                String self_text = currentItem.getmSelf_text();
                //viewHolder.author.setText(authors.get(position));
                // System.out.println("Clickable link? " + clickable_link.get(viewHolder.getAdapterPosition()));
                if (currentItem.getmClickable_link().equals("yes")) {
                    //if there is a clickable link set the textview to be clickable
                    if (self_text.contains("youtube.com")) {
                        System.out.println("Reddit link is a youtube link");
                    }
                    viewHolder.self_text.setClickable(true);
                    String link = "<a href='" + self_text + "'>" + self_text + " </a>";
                    viewHolder.self_text.setText((Html.fromHtml(link)));
                    viewHolder.self_text.setMovementMethod(LinkMovementMethod.getInstance());
                    viewHolder.self_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.e(TAG, "Self text clicked");
                            sendEngagementData(DIRECT_LINK_TAP);
                        }
                    });
                } else {
                    viewHolder.self_text.setText(self_text);
                    viewHolder.self_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.e(TAG, "Self text clicked");
                            sendEngagementData(DIRECT_LINK_TAP);
                        }
                    });
                }

                viewHolder.date_text.setText(currentItem.getmDate());
                //viewHolder.trending_level.setText(currentItem.getmTrending_level());
                viewHolder.post_title.setText(currentItem.getmPost_title());
                final String link = "https://www.reddit.com" + currentItem.getmPermalink();
                viewHolder.goto_reddit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendEngagementData(REDDIT_BUTTON_TAP);
                        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                        Bundle params = new Bundle();
                        params.putString("GoToRedditButton", link);
                        firebaseAnalytics.logEvent("linkTapped", params);

                        Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }
                });
                viewHolder.reddit_share_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendEngagementData(SHARE_BUTTON_TAP);
                        initShareButton(link + " via LeaugePulse", "LeaguePulse");
                    }
                });
            } else if (viewHolder1.getItemViewType() == VIEW_TYPE_TWITTER) {
                TwitterViewHolder viewHolder = (TwitterViewHolder) viewHolder1;
                if (dark_theme_enabled) {
                    System.out.println("Dark theme enabled for TwitterCardView");
                    viewHolder.parent_layout.setBackgroundColor(Color.parseColor("#252525"));
                    viewHolder.twitter_name.setTextColor(Color.parseColor("#FFFFFF"));
                    viewHolder.twitter_handle.setTextColor(Color.parseColor("#FFFFFF"));
                    viewHolder.self_text.setTextColor(Color.parseColor("#ACACAC"));
                    viewHolder.date_text.setTextColor(Color.parseColor("#ACACAC"));
                } else {
                    viewHolder.parent_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    viewHolder.twitter_name.setTextColor(Color.parseColor("#000000"));
                    viewHolder.twitter_handle.setTextColor(Color.parseColor("#000000"));
                    viewHolder.self_text.setTextColor(Color.parseColor("#515050"));
                    viewHolder.date_text.setTextColor(Color.parseColor("#515050"));
                }
                System.out.print(currentItem.getmTwitter_name() + " " + currentItem.getmMedia_type());
                Picasso.get().load(currentItem.getmUser_profile_pic_url())
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .into(viewHolder.profile_pic);
                //  Glide.with(context).load(currentItem.getmUser_profile_pic_url()).into(viewHolder.profile_pic);
                String media_type = currentItem.getmMedia_type();
                System.out.print("running setmedia for: " + currentItem.getmTwitter_name() + "--");
                viewHolder.video_view.setVisibility(View.GONE);
                viewHolder.picture_imageview.setVisibility(View.GONE);
                setMedia(media_type, currentItem.getmTwitter_media_url(), viewHolder, currentItem,
                        currentItem.getmTwitter_name());

                viewHolder.self_text.setText(currentItem.getmSelf_text());
                viewHolder.twitter_handle.setText(currentItem.getmTwitter_handle());
                viewHolder.twitter_name.setText(currentItem.getmTwitter_name());
                viewHolder.date_text.setText(currentItem.getmDate());
                final String link = currentItem.getmPermalink();
                viewHolder.goto_twitter_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendEngagementData(TWITTER_BUTTON_TAP);
                        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                        Bundle params = new Bundle();
                        params.putString("GoToTwitterButton", link);
                        firebaseAnalytics.logEvent("linkTapped", params);
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }
                });
                viewHolder.twitter_share_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendEngagementData(SHARE_BUTTON_TAP);
                        initShareButton(link + " via LeaugePulse", "LeaguePulse");
                    }
                });
            } else {
                final ProGameViewHolder viewHolder = (ProGameViewHolder) viewHolder1;
                if (dark_theme_enabled) {
                    System.out.println("Dark theme enabled for ProGameCardView");
                    viewHolder.parent_layout.setBackgroundColor(Color.parseColor("#252525"));
                    viewHolder.title_text.setTextColor(Color.parseColor("#FFFFFF"));
                    viewHolder.week_region_text.setTextColor(Color.parseColor("#ACACAC"));
                    viewHolder.winner_line_text.setTextColor(Color.parseColor("#FFFFFF"));
                } else {

                    viewHolder.parent_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    viewHolder.title_text.setTextColor(Color.parseColor("#000000"));
                    viewHolder.week_region_text.setTextColor(Color.parseColor("#515050"));
                    viewHolder.winner_line_text.setTextColor(Color.parseColor("#000000"));
                }
                viewHolder.winner_line_text.setText(currentItem.getmWinner_line());
                viewHolder.week_region_text.setText(currentItem.getmWeek_region());
                viewHolder.show_winner.setVisibility(View.VISIBLE);
                viewHolder.winner_line_text.setVisibility(View.GONE);
                viewHolder.title_text.setText(currentItem.getmTeam1() + " vs. " + currentItem.getmTeam2());
                final String link = "https://www.reddit.com" + currentItem.getmPermalink();
                viewHolder.match_details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendEngagementData(REDDIT_BUTTON_TAP);
                        Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }
                });
                viewHolder.progame_share_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendEngagementData(SHARE_BUTTON_TAP);
                        initShareButton(link + " via LeaugePulse", "LeaguePulse");
                    }
                });
                viewHolder.show_winner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolder.show_winner.setVisibility(View.GONE);
                        viewHolder.winner_line_text.setVisibility(View.VISIBLE);
                        if (dark_theme_enabled) {
                            viewHolder.winner_line_text.setTextColor(Color.parseColor("#FFFFFF"));
                        } else {
                            viewHolder.winner_line_text.setTextColor(Color.parseColor("#000000"));
                        }
                       /* ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(viewHolder.constraintLayout);
                        constraintSet.connect(R.id.pgcv_match_details_button,ConstraintSet.BOTTOM,
                                R.id.pgcv_winner_line,ConstraintSet.TOP,0);*/

                    }
                });
                Picasso.get().load(currentItem.getmTeam1_logo()).placeholder(R.drawable.placeholder2)
                        .into(viewHolder.team1_logo);
                Picasso.get().load(currentItem.getmTeam2_logo()).placeholder(R.drawable.placeholder2)
                        .into(viewHolder.team2_logo);
                //viewHolder.team1_logo.setImageResource(currentItem.getmTeam1_logo());
                //viewHolder.team2_logo.setImageResource(currentItem.getmTeam2_logo());
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        // Log.i(TAG, "bindView time: " + (System.currentTimeMillis() - startTime));
    }

    private void setMedia(String media_type, final String media_url, TwitterViewHolder viewHolder,
                          RecyclerItem currentItem, final String twitter_name) {
        SimpleExoPlayer exoPlayer;
        Uri uri;
        if (media_type.equals("photo") && !media_url.equals("")) {
            System.out.println("IN PHOTO: ");
            try {
                // viewHolder.video_view.setVisibility(View.GONE);
                //.resize(1920,1080).onlyScaleDown()
                Picasso.get().load(media_url)
                        .placeholder(R.drawable.placeholder2).into(viewHolder.picture_imageview);
                viewHolder.picture_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        sendEngagementData(OPEN_PHOTO_TAP);
                        Bundle bundle = new Bundle();
                        bundle.putString("image_url", media_url);
                        bundle.putString("twitter_name", twitter_name);
                        Fragment fragment = new ImageFragment();
                        fragment.setArguments(bundle);
                        /*activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_frame_layout, fragment, "IMAGE_FRAGMENT")
                                .commit();*/
                        activity.getSupportFragmentManager().beginTransaction()
                                .add(R.id.main_frame_layout, fragment, "IMAGE_FRAGMENT").commit();

                    }
                });
                viewHolder.picture_imageview.setVisibility(View.VISIBLE);
                //Glide.with(context).load(media_url).into(viewHolder.picture_imageview)
            } catch (Exception e) {
                viewHolder.picture_imageview.setVisibility(View.GONE);
                Log.e(TAG, "Error setting photo");
                Log.e(TAG, e.toString());
            }
        } else if (media_type.equals("video") && !media_url.equals("")) {
            // viewHolder.picture_imageview.setVisibility(View.GONE);
            PlayerView playerView = viewHolder.video_view;
            try {
                uri = Uri.parse(currentItem.getmTwitter_media_url());
                System.out.println("Media video: " + currentItem.getmTwitter_media_url());
                exoPlayer = ExoPlayerFactory.newSimpleInstance(context, selector);
                MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
                playerView.setPlayer(exoPlayer);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(false);
                playerView.hideController();
            } catch (Exception e) {
                playerView.setVisibility(View.GONE);
                Log.e(TAG, "Error setting video");
                Log.e(TAG, e.toString());
            }
        } else if (media_type.equals("animated_gif") && !media_url.equals("")) {
            // viewHolder.picture_imageview.setVisibility(View.GONE);
            PlayerView playerView = viewHolder.video_view;
            try {
                System.out.println("Media type is gif");
                System.out.println("Media video: " + currentItem.getmTwitter_media_url());
                uri = Uri.parse(currentItem.getmTwitter_media_url());
                exoPlayer = ExoPlayerFactory.newSimpleInstance(context, selector);
                MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
                LoopingMediaSource loopSource = new LoopingMediaSource(mediaSource);
                playerView.setPlayer(exoPlayer);
                exoPlayer.prepare(loopSource);
                exoPlayer.seekTo(0);
                exoPlayer.setPlayWhenReady(true);
                playerView.hideController();
                playerView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                playerView.setVisibility(View.GONE);
                Log.e(TAG, "Error setting video");
                Log.e(TAG, e.toString());
            }
        } else {
            // viewHolder.video_view.setVisibility(View.GONE);
            // viewHolder.picture_imageview.setVisibility(View.GONE);
            System.out.println(" else?");
        }

    }

    private void initShareButton(String share_body, String share_subject) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, share_subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, share_body);
        context.startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    @Override
    public int getItemCount() {
        return mRecycleritems.size();
    }


    class RedditViewHolder extends RecyclerView.ViewHolder {

        TextView post_title;
        TextView self_text;
        TextView date_text;
        // TextView trending_level;
        Button goto_reddit_button;
        CardView parent_layout;
        AppCompatImageButton reddit_share_button;

        RedditViewHolder(@NonNull View itemView) {
            super(itemView);
            //trending_level = itemView.findViewById(R.id.cv_trending_level);
            reddit_share_button = itemView.findViewById(R.id.reddit_share_button);
            date_text = itemView.findViewById(R.id.cv_time_posted_text);
            self_text = itemView.findViewById(R.id.cv_self_text);
            post_title = itemView.findViewById(R.id.cv_post_title);
            goto_reddit_button = itemView.findViewById(R.id.cv_gotoreddit_button);
            parent_layout = itemView.findViewById(R.id.cv_parent_layout);
        }
    }

    class TwitterViewHolder extends RecyclerView.ViewHolder {

        TextView twitter_name;
        TextView twitter_handle;
        TextView self_text;
        TextView date_text;
        ImageView picture_imageview;
        CircleImageView profile_pic;
        //ImageView profile_pic;
        PlayerView video_view;
        Button goto_twitter_button;
        CardView parent_layout;
        AppCompatImageButton twitter_share_button;

        TwitterViewHolder(@NonNull View itemView) {
            super(itemView);
            twitter_share_button = itemView.findViewById(R.id.twitter_share_button);
            date_text = itemView.findViewById(R.id.tcv_date_time);
            self_text = itemView.findViewById(R.id.tcv_self_text);
            profile_pic = itemView.findViewById(R.id.tcv_profile_pic);
            twitter_name = itemView.findViewById(R.id.tcv_twitter_name);
            twitter_handle = itemView.findViewById(R.id.tcv_twitter_handle);
            picture_imageview = itemView.findViewById(R.id.tcv_media_image);
            video_view = itemView.findViewById(R.id.tcv_media_video);
            goto_twitter_button = itemView.findViewById(R.id.tcv_goto_twitter_button);
            parent_layout = itemView.findViewById(R.id.tcv_parent_layout);
        }
    }

    class ProGameViewHolder extends RecyclerView.ViewHolder {
        ImageView team1_logo;
        ImageView team2_logo;
        Button match_details;
        Button show_winner;
        TextView title_text;
        TextView week_region_text;
        TextView winner_line_text;
        ConstraintLayout constraintLayout;
        CardView parent_layout;
        AppCompatImageButton progame_share_button;

        ProGameViewHolder(@NonNull View itemView) {
            super(itemView);
            progame_share_button = itemView.findViewById(R.id.progame_share_button);
            constraintLayout = itemView.findViewById(R.id.pgcv_main_constraint_layout);
            match_details = itemView.findViewById(R.id.pgcv_match_details_button);
            show_winner = itemView.findViewById(R.id.show_winner_button);
            team1_logo = itemView.findViewById(R.id.pgcv_team1_logo);
            team2_logo = itemView.findViewById(R.id.pgcv_team2_logo);
            title_text = itemView.findViewById(R.id.pgcv_title);
            week_region_text = itemView.findViewById(R.id.pgcv_week_region_text);
            winner_line_text = itemView.findViewById(R.id.pgcv_winner_line);
            parent_layout = itemView.findViewById(R.id.pgcv_parent_layout);
        }
    }
}
