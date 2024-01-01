package com.project.shortpress.Adapters;

import static com.project.shortpress.Config.URL.deleteMarkArticle_URL;

import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;
import android.util.Log;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.project.shortpress.Activities.ShowArticleContentActivity;
import com.project.shortpress.Config.FindDateDiff;
import com.project.shortpress.Models.NewsModel;
import com.project.shortpress.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class SavedNewsAdapter extends RecyclerView.Adapter<SavedNewsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<NewsModel> newsModelArrayList;
    private RequestQueue requestQueue;
    private final FindDateDiff findDateDiff = new FindDateDiff();

    // Constructor
    public SavedNewsAdapter(Context context, ArrayList<NewsModel> courseModelArrayList) {
        this.context = context;
        this.newsModelArrayList = courseModelArrayList;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void deleteBookmark(String bid,int position){
        Log.i("Status::","getSummarize Initiated");
        try{
        //String ArticleURL = article.getUrl();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("bid", bid);
        final String requestBody = jsonBody.toString();
        Log.i("ReqContent>>",requestBody);

        StringRequest request = new StringRequest(Request.Method.POST, deleteMarkArticle_URL,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.i("RespContent>>", response);
                        Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                        newsModelArrayList.remove(position);
                        notifyItemRemoved(position);

                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error:",error.toString());
                    }
                }
        ) {
            public byte[] getBody() {
                return requestBody.getBytes();
            }
            public String getBodyContentType() {
                return "text/plain";
            }
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map<String, String> mHeaders = new ArrayMap<String, String>();
                mHeaders.put("User-Agent", "Mozilla/5.0");
                return mHeaders;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);}
        catch (Exception e){

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_saved_card_layout, parent, false);
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

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBookmark(model.getBid(),pos);

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
        private final ImageButton deleteButton;
        private final ConstraintLayout cardItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImg = itemView.findViewById(R.id.newsimgview);
            newsTitle = itemView.findViewById(R.id.newstitletv);
            newsTime = itemView.findViewById(R.id.newstimetv);
            //newsPrefText =itemView.findViewById(R.id.newspref);
            cardItem = itemView.findViewById(R.id.cardItem);
            shareButton= itemView.findViewById(R.id.sharebutton);
            deleteButton= itemView.findViewById(R.id.deleteSavedButton);
        }
    }
}
