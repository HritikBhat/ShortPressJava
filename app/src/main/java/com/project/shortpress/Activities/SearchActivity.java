package com.project.shortpress.Activities;

import static com.project.shortpress.Config.URL.getSearchArticles_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.project.shortpress.Models.NewsModel;
import com.project.shortpress.R;
import com.project.shortpress.Config.SharedPrefFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity {
    ArrayList<NewsModel> newsArr;
    SharedPrefFunctions sharedPrefFunctions;
    RecyclerView recyclerView;
    SearchNewsAdapter adapter;
    RequestQueue requestQueue;
    EditText searchText;
    ImageView backBtn;

    //This function will show the articles according to search result
    private void showArticle(){
        adapter = new SearchNewsAdapter(getApplicationContext(),newsArr);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    //This function will retrieve articles by search query
    protected void searchArticles(String query) throws JSONException {
        newsArr.clear();
        Log.i("Status::","getSummarize Initiated");

        //String ArticleURL = article.getUrl();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("searchQuery", query);
        final String requestBody = jsonBody.toString();
        Log.i("ReqContent>>",requestBody);

        StringRequest request = new StringRequest(Request.Method.POST, getSearchArticles_URL,
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
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.searchRV);
        searchText= findViewById(R.id.searchEditText);
        newsArr=new ArrayList<>();

        searchText.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer = new Timer();
                    private final long DELAY = 1000; // Milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (searchText.getText().toString().length()==0){
                                                newsArr.clear();
                                            }
                                            else {
                                                searchArticles(searchText.getText().toString());
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                DELAY
                        );
                    }
                }
        );

        sharedPrefFunctions=new SharedPrefFunctions(getApplicationContext());
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        backBtn=findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}