package com.project.shortpress.Activities;

import static com.project.shortpress.Config.URL.getCategoryArticles_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.shortpress.Adapters.SearchNewsAdapter;
import com.project.shortpress.Config.SharedPrefFunctions;
import com.project.shortpress.Models.NewsModel;
import com.project.shortpress.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {
    ImageView dayNightModeBtn,backBtn;
    Intent intent;
    Bundle extras;
    String cat;
    ArrayList<NewsModel> newsArr;
    SharedPrefFunctions sharedPrefFunctions;
    RecyclerView recyclerView;
    SearchNewsAdapter adapter;
    RequestQueue requestQueue;


    //This function will show the articles according to category
    private void showArticle(){
        adapter = new SearchNewsAdapter(getApplicationContext(),newsArr);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    //This function will retrieve articles from particular category
    protected void getUserPrefData() throws JSONException {
        Log.i("Status::","getSummarize Initiated");

        //String ArticleURL = article.getUrl();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cat", cat);
        final String requestBody = jsonBody.toString();
        Log.i("ReqContent>>",requestBody);

        StringRequest request = new StringRequest(Request.Method.POST, getCategoryArticles_URL,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.i("RespContent>>", response);
                        JSONArray jsonArray = null;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonArray = jsonObject.getJSONArray("articles");
                            for(int i =0;i<jsonArray.length();i++){
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                NewsModel newsModel = new NewsModel();
                                newsModel.setAuthor(jsonObject1.getString("author"));
                                newsModel.setTitle(jsonObject1.getString("title"));
                                newsModel.setPref("");
                                newsModel.setPublishedAt(jsonObject1.getString("publishedAt"));
                                newsModel.setUrl(jsonObject1.getString("url"));
                                newsModel.setUrlToImage(jsonObject1.getString("urlToImage"));
                                newsArr.add(newsModel);
                            }
                            showArticle();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
        requestQueue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        sharedPrefFunctions =new SharedPrefFunctions(getApplicationContext());
        intent = getIntent();
        extras = intent.getExtras();
        //Getting category name from the last page
        cat = extras.getString("Cat");

        dayNightModeBtn= findViewById(R.id.modeChangeBtn);

        if (sharedPrefFunctions.getIsDarkModeOn()) {
            dayNightModeBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_wb_sunny_24,getTheme()));
        }
        else {
            dayNightModeBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_nights_stay_24,getTheme()));

        }
        dayNightModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedPrefFunctions.getIsDarkModeOn()) {
                    // if dark mode is on it
                    // will turn it off
                    dayNightModeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_nights_stay_24));
                    sharedPrefFunctions.setIsDarkModeOn(false);
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_NO);

                }
                else {
                    sharedPrefFunctions.setIsDarkModeOn(true);
                    dayNightModeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_wb_sunny_24));
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_YES);

                }

            }
        });

        backBtn=findViewById(R.id.backBtn_ab);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView =  findViewById(R.id.catRV);
        newsArr=new ArrayList<>();
        /*
        catNameTV = findViewById(R.id.catNameTV);
        catNameTV.setText(cat);
         */

        sharedPrefFunctions=new SharedPrefFunctions(getApplicationContext());
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            getUserPrefData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}