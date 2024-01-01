package com.project.shortpress.Fragments.Home;

import static com.project.shortpress.Config.URL.getUserPrefArticles_URL;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.shortpress.Adapters.NewsAdapter;
import com.project.shortpress.Models.NewsModel;
import com.project.shortpress.R;
import com.project.shortpress.Config.SharedPrefFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class FeedFragment extends Fragment {
    ArrayList<NewsModel> newsArr;
    SharedPrefFunctions sharedPrefFunctions;
    RecyclerView recyclerView;
    NewsAdapter adapter;
    RequestQueue requestQueue;


    private void showArticle(){
        adapter = new NewsAdapter(getActivity(),newsArr);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    protected void getUserPrefData() throws JSONException {
        Log.i("Status::","getSummarize Initiated");

        //String ArticleURL = article.getUrl();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("uid", sharedPrefFunctions.getUserId());
        final String requestBody = jsonBody.toString();
        Log.i("ReqContent>>",requestBody);

        StringRequest request = new StringRequest(Request.Method.POST, getUserPrefArticles_URL,
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.newsRV);
        newsArr=new ArrayList<>();

        sharedPrefFunctions=new SharedPrefFunctions(getActivity());
        requestQueue = Volley.newRequestQueue(getActivity());
        try {
            getUserPrefData();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
    }
}