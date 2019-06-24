package us.williamtucker.leaguepulse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.leagepulse.leaguepulse.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import us.williamtucker.leaguepulse.data.RecyclerItem;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private final int VIEW_TYPE_REDDIT = 0;
    private final int VIEW_TYPE_TWITTER = 1;
    private final int VIEW_TYPE_PROGAME = 2;
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

    RecyclerViewAdapter(ArrayList<RecyclerItem> recyclerItems, Context context) {
        mRecycleritems = recyclerItems;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //System.out.println("1234asd..."+mRecycleritems.get(0).getmSelf_text());
        //inflates our view
        View view;
        RecyclerView.ViewHolder view_holder;

        if(viewType == VIEW_TYPE_TWITTER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.twitter_cardview,
                    parent, false);
            view_holder = new TwitterViewHolder(view);
        }else if(viewType==VIEW_TYPE_REDDIT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reddit_cardview,
                    parent, false);
            view_holder = new RedditViewHolder(view);
        }else{
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder1, int position) {
        final RecyclerItem currentItem = mRecycleritems.get(position);
        if (viewHolder1.getItemViewType() == VIEW_TYPE_REDDIT) {
            RedditViewHolder viewHolder = (RedditViewHolder) viewHolder1;
            String self_text = currentItem.getmSelf_text();
            //viewHolder.author.setText(authors.get(position));
            // System.out.println("Clickable link? " + clickable_link.get(viewHolder.getAdapterPosition()));
            if (currentItem.getmClickable_link().equals("yes")) {
                //if there is a clickable link set the textview to be clickable

                viewHolder.self_text.setClickable(true);
                viewHolder.self_text.setMovementMethod(LinkMovementMethod.getInstance());
                String link = "<a href='" + self_text + "'>" + self_text + " </a>";
                viewHolder.self_text.setText((Html.fromHtml(link)));
            } else {
                viewHolder.self_text.setText(self_text);
            }
            viewHolder.date_text.setText(currentItem.getmDate());
            viewHolder.trending_level.setText(currentItem.getmTrending_level());
            viewHolder.post_title.setText(currentItem.getmPost_title());
            viewHolder.goto_reddit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String link = "https://www.reddit.com" + currentItem.getmPermalink();
                    Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
            });
        } else if (viewHolder1.getItemViewType() == VIEW_TYPE_TWITTER) {
            System.out.print(currentItem.getmTwitter_name() + " " + currentItem.getmMedia_type());
            TwitterViewHolder viewHolder = (TwitterViewHolder) viewHolder1;
            Picasso.get().load(currentItem.getmUser_profile_pic_url())
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .into(viewHolder.profile_pic);
            String media_type = currentItem.getmMedia_type();
            System.out.print("running setmedia for: " + currentItem.getmTwitter_name() + "--");
            setMedia(media_type, currentItem.getmTwitter_media_url(), viewHolder, currentItem);

            viewHolder.self_text.setText(currentItem.getmSelf_text());
            viewHolder.twitter_handle.setText(currentItem.getmTwitter_handle());
            viewHolder.twitter_name.setText(currentItem.getmTwitter_name());
            viewHolder.date_text.setText(currentItem.getmDate());
            viewHolder.goto_twitter_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String link = currentItem.getmPermalink();
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
            });
        }else{
            ProGameViewHolder viewHolder = (ProGameViewHolder) viewHolder1;
            viewHolder.winner_line_text.setText(currentItem.getmWinner_line());
            viewHolder.week_region_text.setText(currentItem.getmWeek_region());
            viewHolder.title_text.setText(currentItem.getmTeam1() + " vs. "+currentItem.getmTeam2());
            viewHolder.team1_logo.setImageResource(currentItem.getmTeam1_logo());
            viewHolder.team2_logo.setImageResource(currentItem.getmTeam2_logo());
        }
    }
    private void setMedia(String media_type, String media_url, TwitterViewHolder viewHolder,
                          RecyclerItem currentItem) {
        SimpleExoPlayer exoPlayer;
        Uri uri;
        if (media_type.equals("photo") && !media_url.equals("")) {
            viewHolder.video_view.setVisibility(View.GONE);
            System.out.println("Media type is photo");
            viewHolder.picture_imageview.setVisibility(View.VISIBLE);
            Picasso.get().load(media_url)
                    .placeholder(R.drawable.placeholder2).into(viewHolder.picture_imageview);
        } else if (media_type.equals("video") && !media_url.equals("")) {
            viewHolder.picture_imageview.setVisibility(View.GONE);
            uri = Uri.parse(currentItem.getmTwitter_media_url());
            System.out.println("Media video: " + currentItem.getmTwitter_media_url());
            PlayerView playerView = viewHolder.video_view;
            exoPlayer = ExoPlayerFactory.newSimpleInstance(context, selector);
            MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
            playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(false);
            playerView.hideController();
            playerView.setVisibility(View.VISIBLE);
        } else if (media_type.equals("animated_gif") && !media_url.equals("")) {
            viewHolder.picture_imageview.setVisibility(View.GONE);
            System.out.println("Media type is gif");
            System.out.println("Media video: " + currentItem.getmTwitter_media_url());
            uri = Uri.parse(currentItem.getmTwitter_media_url());
            PlayerView playerView = viewHolder.video_view;
            exoPlayer = ExoPlayerFactory.newSimpleInstance(context, selector);
            MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
            playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.seekTo(0);
            exoPlayer.setPlayWhenReady(true);
            playerView.hideController();
            playerView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.video_view.setVisibility(View.GONE);
            viewHolder.picture_imageview.setVisibility(View.GONE);
            System.out.println(" else?");
        }
    }

/*
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        final RecyclerItem currentItem = mRecycleritems.get(position);
        String self_text = currentItem.getmSelf_text();
        //viewHolder.author.setText(authors.get(position));
        // System.out.println("Clickable link? " + clickable_link.get(viewHolder.getAdapterPosition()));
        if (currentItem.getmClickable_link().equals("yes")) {
            //if there is a clickable link set the textview to be clickable

            viewHolder.self_text.setClickable(true);
            viewHolder.self_text.setMovementMethod(LinkMovementMethod.getInstance());
            String link = "<a href='" + self_text + "'>" + self_text + " </a>";
            viewHolder.self_text.setText((Html.fromHtml(link)));
        } else {
            viewHolder.self_text.setText(self_text);
        }
        viewHolder.date_text.setText(currentItem.getmDate());
        viewHolder.trending_level.setText(currentItem.getmTrending_level());
        viewHolder.post_title.setText(currentItem.getmPost_title());
        viewHolder.goto_reddit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = "https://www.reddit.com" + currentItem.getmLink_to_reddit();
                Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
       /* viewHolder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + currentItem.getmPost_title());
            }
        });
    }*/

    @Override
    public int getItemCount() {
        return mRecycleritems.size();
    }


    class RedditViewHolder extends RecyclerView.ViewHolder {

        TextView post_title;
        TextView self_text;
        TextView date_text;
        TextView trending_level;
        Button goto_reddit_button;
        CardView parent_layout;

        RedditViewHolder(@NonNull View itemView) {
            super(itemView);
            trending_level = itemView.findViewById(R.id.cv_trending_level);
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

        TwitterViewHolder(@NonNull View itemView) {
            super(itemView);
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
        TextView title_text;
        TextView week_region_text;
        TextView winner_line_text;
        ProGameViewHolder(@NonNull View itemView) {
            super(itemView);
            team1_logo = itemView.findViewById(R.id.pgcv_team1_logo);
            team2_logo = itemView.findViewById(R.id.pgcv_team2_logo);
            title_text = itemView.findViewById(R.id.pgcv_title);
            week_region_text = itemView.findViewById(R.id.pgcv_week_region_text);
            winner_line_text = itemView.findViewById(R.id.pgcv_winner_line);
        }
    }
}