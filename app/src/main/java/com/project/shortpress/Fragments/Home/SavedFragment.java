package com.project.shortpress.Fragments.Home;

import static com.project.shortpress.Config.URL.getBookMarkArticles_URL;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.shortpress.Adapters.SavedNewsAdapter;
import com.project.shortpress.Models.NewsModel;
import com.project.shortpress.R;
import com.project.shortpress.Config.SharedPrefFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class SavedFragment extends Fragment {
    ArrayList<NewsModel> newsArr;
    SharedPrefFunctions sharedPrefFunctions;
    RecyclerView recyclerView;
    SavedNewsAdapter adapter;
    RequestQueue requestQueue;


    private void showSavedArticle(){
        adapter = new SavedNewsAdapter(getActivity(),newsArr);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    protected void getSavedArticles() throws JSONException {
        Log.i("Status::","getSummarize Initiated");

        //String ArticleURL = article.getUrl();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("uid", sharedPrefFunctions.getUserId());
        final String requestBody = jsonBody.toString();
        Log.i("ReqContent>>",requestBody);

        StringRequest request = new StringRequest(Request.Method.POST, getBookMarkArticles_URL,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.i("RespContent>>", response);
                        JSONArray jsonArray = null;
                        try {
                            if (!response.contains("Empty")) {
                                jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    NewsModel newsModel = new NewsModel();
                                    newsModel.setBid(jsonObject.getString("bid"));
                                    newsModel.setAuthor(jsonObject.getString("artauthor"));
                                    newsModel.setTitle(jsonObject.getString("artname"));
                                    newsModel.setPref("");
                                    newsModel.setPublishedAt(jsonObject.getString("artdate"));
                                    newsModel.setUrl(jsonObject.getString("artURL"));
                                    newsModel.setUrlToImage(jsonObject.getString("artImageURL"));
                                    newsArr.add(newsModel);
                                }
                                showSavedArticle();
                            }
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
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        requestQueue = Volley.newRequestQueue(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.newsSavedRV);
        newsArr=new ArrayList<>();

        sharedPrefFunctions = new SharedPrefFunctions(getActivity());

        try {
            getSavedArticles();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        SavedNewsAdapter adapter = new SavedNewsAdapter(getActivity(),newsArr);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


        return view;
    }
}