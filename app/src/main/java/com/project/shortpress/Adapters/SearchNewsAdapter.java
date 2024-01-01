package com.project.shortpress.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.project.shortpress.Activities.ShowArticleContentActivity;
import com.project.shortpress.Config.FindDateDiff;
import com.project.shortpress.Models.NewsModel;
import com.project.shortpress.R;

import java.util.ArrayList;

public class SearchNewsAdapter extends RecyclerView.Adapter<SearchNewsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<NewsModel> newsModelArrayList;
    private RequestQueue requestQueue;
    private final FindDateDiff findDateDiff = new FindDateDiff();

    // Constructor
    public SearchNewsAdapter(Context context, ArrayList<NewsModel> courseModelArrayList) {
        this.context = context;
        this.newsModelArrayList = courseModelArrayList;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int pos =position;
        // to set data to textview and imageview of each card layout
        NewsModel model = newsModelArrayList.get(position);
        holder.newsTitle.setText(model.getTitle());
        String dateDiff = findDateDiff.findDifference(model.getPublishedAt());

        holder.newsTime.setText(dateDiff);
        //holder.newsPrefText.setText(model.getPref());
        Glide.with(context)
                .load(model.getUrlToImage())
                .into(holder.newsImg);

        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowArticleContentActivity.class);
                intent.putExtra("NewsModel", model);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return newsModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView newsImg;
        private final TextView newsTitle;
        private final TextView newsTime;
        //private final TextView newsPrefText;
        private final ImageButton shareButton;
        private final ConstraintLayout cardItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImg = itemView.findViewById(R.id.newsimgview);
            newsTitle = itemView.findViewById(R.id.newstitletv);
            newsTime = itemView.findViewById(R.id.newstimetv);
            //newsPrefText =itemView.findViewById(R.id.newspref);
            cardItem = itemView.findViewById(R.id.cardItem);
            shareButton= itemView.findViewById(R.id.sharebutton);
        }
    }
}
