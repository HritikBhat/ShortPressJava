package com.project.shortpress.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.shortpress.Activities.ShowArticleContentActivity;
import com.project.shortpress.Config.FindDateDiff;
import com.project.shortpress.Models.NewsModel;
import com.project.shortpress.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    boolean setheadersdata_flag=false;

    private final Context context;
    private final ArrayList<NewsModel> newsModelArrayList;
    private final FindDateDiff findDateDiff = new FindDateDiff();

    public NewsAdapter(Context context, ArrayList<NewsModel> newsModelArrayList) {
        this.context = context;
        this.newsModelArrayList = newsModelArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            // Here Inflating your recyclerview item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_layout, parent, false);
            return new ItemViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            // Here Inflating your header view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_headeritem, parent, false);
            return new HeaderViewHolder(itemView);
        }
        else return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderViewHolder){
            setheadersdata_flag = true;
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
            Calendar c = Calendar.getInstance();
            String date = sdf.format(c.getTime());

            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.dateTextView.setText(date);


        }
        else if (holder instanceof ItemViewHolder){

            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            NewsModel model = newsModelArrayList.get(position);
            itemViewHolder.newsTitle.setText(model.getTitle());
            String dateDiff = findDateDiff.findDifference(model.getPublishedAt());
            itemViewHolder.newsTime.setText(dateDiff);
            Glide.with(context)
                    .load(model.getUrlToImage())
                    .into(itemViewHolder.newsImg);

            itemViewHolder.cardItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ShowArticleContentActivity.class);
                    intent.putExtra("NewsModel", model);
                    context.startActivity(intent);
                }
            });

            itemViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, model.getTitle());
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, model.getUrl());
                    context.startActivity(Intent.createChooser(sharingIntent, "Sharing"));
                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }


    // getItemCount increasing the position to 1. This will be the row of header
    @Override
    public int getItemCount() {
        return newsModelArrayList.size();
    }


    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        public HeaderViewHolder(View view) {
            super(view);
            dateTextView = itemView.findViewById(R.id.dateTextView);

        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView newsImg;
        private final TextView newsTitle;
        private final TextView newsTime;
        private final ImageButton shareButton;
        private final ConstraintLayout cardItem;
        public ItemViewHolder(View itemView) {
            super(itemView);
            newsImg = itemView.findViewById(R.id.newsimgview);
            newsTitle = itemView.findViewById(R.id.newstitletv);
            newsTime = itemView.findViewById(R.id.newstimetv);
            cardItem = itemView.findViewById(R.id.cardItem);
            shareButton= itemView.findViewById(R.id.sharebutton);
        }
    }
}