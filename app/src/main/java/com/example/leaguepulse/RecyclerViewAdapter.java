package com.example.leaguepulse;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> post_titles;
    private ArrayList<String> self_texts;
    private ArrayList<String> authors;
    private ArrayList<String> dates;
    private ArrayList<String> link_to_reddit;
    private ArrayList<String> clickable_link;
    private ArrayList<String> trending_levels;
    private Context context;

    RecyclerViewAdapter(ArrayList<String> post_titles, ArrayList<String> self_texts,
                        ArrayList<String> authors, ArrayList<String> clickable_link, Context context,
                        ArrayList<String> link_to_reddit, ArrayList<String> dates,
                        ArrayList<String> trending_levels) {
        this.clickable_link = clickable_link;
        this.link_to_reddit = link_to_reddit;
        this.trending_levels = trending_levels;
        this.authors = authors;
        this.dates = dates;
        this.post_titles = post_titles;
        this.self_texts = self_texts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        //inflates our view

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reddit_cardview,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        //viewHolder.author.setText(authors.get(position));
        System.out.println("Clickable link? " + clickable_link.get(viewHolder.getAdapterPosition()));
        if (clickable_link.get(viewHolder.getAdapterPosition()).equals("yes")) {
            //if there is a clickable link set the textview to be clickable

            viewHolder.self_text.setClickable(true);
            viewHolder.self_text.setMovementMethod(LinkMovementMethod.getInstance());
            String link = "<a href='" + self_texts.get(viewHolder.getAdapterPosition()) + "'>" + self_texts.get(viewHolder.getAdapterPosition()) + " </a>";
            viewHolder.self_text.setText((Html.fromHtml(link)));
        } else {
            viewHolder.self_text.setText(self_texts.get(viewHolder.getAdapterPosition()));
        }
        viewHolder.date_text.setText(dates.get(viewHolder.getAdapterPosition()));
        viewHolder.trending_level.setText(trending_levels.get(viewHolder.getAdapterPosition()));
        viewHolder.post_title.setText(post_titles.get(viewHolder.getAdapterPosition()));
        viewHolder.goto_reddit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = "https://www.reddit.com" + link_to_reddit.get(viewHolder.getAdapterPosition());
                Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
        viewHolder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + post_titles.get(viewHolder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return post_titles.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView post_title;
        TextView self_text;
        TextView date_text;
        TextView trending_level;
        Button goto_reddit_button;
        //TextView author;
        CardView parent_layout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            //author = itemView.findViewById(R.id.author_text);
            trending_level = itemView.findViewById(R.id.cv_trending_level);
            date_text = itemView.findViewById(R.id.cv_time_posted_text);
            self_text = itemView.findViewById(R.id.cv_self_text);
            post_title = itemView.findViewById(R.id.cv_post_title);
            goto_reddit_button = itemView.findViewById(R.id.cv_gotoreddit_button);
            parent_layout = itemView.findViewById(R.id.cv_parent_layout);
        }
    }
}
