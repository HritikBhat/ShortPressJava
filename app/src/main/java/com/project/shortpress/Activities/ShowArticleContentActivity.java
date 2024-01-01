package com.project.shortpress.Activities;

import static com.project.shortpress.Config.URL.addBookMarkArticle_URL;
import static com.project.shortpress.Config.URL.isArticleBookmarked_URL;
import static com.project.shortpress.Config.URL.removeBookMarkArticle_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.project.shortpress.Config.FindDateDiff;
import com.project.shortpress.Models.NewsModel;
import com.project.shortpress.R;
import com.project.shortpress.Config.SharedPrefFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Timer;

public class ShowArticleContentActivity extends AppCompatActivity {
    int userSelectedLangIndex =-1;
    NewsModel newsModel;
    ImageView newsImg;
    TextView newsTitle;
    TextView newsTime;
    Spinner langSpinner;
    TextView newsContent;
    RequestQueue requestQueue;
    Timer timerObj;
    int load_index=0;
    LottieAnimationView animationView;
    String[] loadText = {"Fetching Data...","Summarizing...","Almost there..."};
    ConstraintLayout loadingCL,contentCL;
    TextView loadingTV;
    ImageButton bookmarkArticleButton,shareArticleButton;
    SharedPrefFunctions sharedPrefFunctions;
    ImageView backBtn;
    Handler handler;
    int isBookmarked = 0;
    String contentEng;
    private final FindDateDiff findDateDiff = new FindDateDiff();

    //This function will translate the text into desired language
    private void translateText(Translator translator){
        translator.translate(contentEng)
                .addOnSuccessListener(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                newsContent.setText(o.toString());
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error.
                                // ...
                            }
                        });
    }

    //This function will set and download required library for translation from google.
    protected void setTranslation(String destLang){
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(destLang)
                        .build();

        Translator translator = Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                translateText(translator);
                            }

                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ShowArticleContentActivity.this, "Downloading Required Resources", Toast.LENGTH_SHORT).show();
                            }
                        });


    }

    //This function will bookmark article for that user.
    protected void addBookmark() throws JSONException {
        Log.i("Status::","getSummarize Initiated");

        //String ArticleURL = article.getUrl();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("uid", sharedPrefFunctions.getUserId());
        jsonBody.put("artdate", newsModel.getPublishedAt());
        jsonBody.put("artname", newsModel.getTitle());
        jsonBody.put("artauthor", newsModel.getAuthor());
        jsonBody.put("artImageURL", newsModel.getUrlToImage());
        jsonBody.put("artURL", newsModel.getUrl());
        final String requestBody = jsonBody.toString();
        Log.i("ReqContent>>",requestBody);

        StringRequest request = new StringRequest(Request.Method.POST, addBookMarkArticle_URL,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.i("RespContent>>", response);
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        bookmarkArticleButton.setImageDrawable(AppCompatResources.getDrawable(ShowArticleContentActivity.this,R.drawable.ic_baseline_bookmarked));
                        isBookmarked = 1;
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

    //This function will remove bookmark article for that user.
    protected void removeBookmark() throws JSONException {
        Log.i("Status::","getSummarize Initiated");

        //String ArticleURL = article.getUrl();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("uid", sharedPrefFunctions.getUserId());
        jsonBody.put("artURL", newsModel.getUrl());
        final String requestBody = jsonBody.toString();
        Log.i("ReqContent>>",requestBody);

        StringRequest request = new StringRequest(Request.Method.POST, removeBookMarkArticle_URL,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.i("RespContent>>", response);
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        bookmarkArticleButton.setImageDrawable(AppCompatResources.getDrawable(ShowArticleContentActivity.this,R.drawable.ic_baseline_addbookmark));
                        isBookmarked = 0;
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
        setContentView(R.layout.activity_show_article_content);

        handler = new Handler();
        sharedPrefFunctions = new SharedPrefFunctions(this);

        animationView= findViewById(R.id.animationView);

        if (sharedPrefFunctions.getIsDarkModeOn()) {

            //dayNightModeBtn.setImageDrawable(AppCompatResources.getDrawable(ShowArticleContentActivity.this,R.drawable.ic_baseline_wb_sunny_24));
            animationView.setAnimation(R.raw.lf30_editor_qnxtaol8_dark);
        }



        backBtn=findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        newsModel= (NewsModel) getIntent().getSerializableExtra("NewsModel");
        newsImg =findViewById(R.id.article_img);
        newsTitle = findViewById(R.id.articleTitle);
        newsTime = findViewById(R.id.articleDatePref);
        String dateDiff = findDateDiff.findDifference(newsModel.getPublishedAt());
        newsTime.setText(dateDiff);
        newsContent = findViewById(R.id.articleContent);
        loadingTV = findViewById(R.id.loading_text);
        loadingCL =findViewById(R.id.constraintLoadingLayout);
        contentCL=findViewById(R.id.constraintContentLayout);
        requestQueue = Volley.newRequestQueue(this);

        shareArticleButton = findViewById(R.id.shareArticleButton);
        shareArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, newsModel.getTitle());
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, newsModel.getUrl());
                    startActivity(Intent.createChooser(sharingIntent, "Sharing"));

                }catch (Exception e){
                    e.printStackTrace();
                }
               }
        });

        bookmarkArticleButton = findViewById(R.id.bookmarkArticleButton);
        bookmarkArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isBookmarked==1){
                        removeBookmark();
                    }
                    else{
                        addBookmark();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });

        try {
            isArticleBookmarked(newsModel.getUrl(),sharedPrefFunctions.getUserId());
            //getSummarizedArticleContent();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Glide.with(this)
                .load(newsModel.getUrlToImage())
                .into(newsImg);

        newsTitle.setText(newsModel.getTitle());

        langSpinner=findViewById(R.id.spinner_languages);

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(userSelectedLangIndex != position){
                    userSelectedLangIndex=position;
                    switch (userSelectedLangIndex){
                        case 0:
                            newsContent.setText(contentEng);
                            break;
                        case 1:
                            setTranslation(TranslateLanguage.HINDI);
                            break;
                        case 2:
                            setTranslation(TranslateLanguage.MARATHI);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        langSpinner.setAdapter(adapter);


    }

    //This function will check if the article is bookmarked for that user or not.
    private void isArticleBookmarked(String url, int userId) throws JSONException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("uid", userId);
        jsonBody.put("artURL", url);
        final String requestBody = jsonBody.toString();
        //Log.i("ReqContent>>",requestBody);

        StringRequest request = new StringRequest(Request.Method.POST,isArticleBookmarked_URL,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.i("RespContent>>", response);
                        if(response.contains("True")){
                            //Toast.makeText(ShowArticleContentActivity.this, "Bookmarked Id", Toast.LENGTH_SHORT).show();
                            bookmarkArticleButton.setImageDrawable(AppCompatResources.getDrawable(ShowArticleContentActivity.this,R.drawable.ic_baseline_bookmarked));
                            isBookmarked = 1;
                        }
                        try {
                            getSummarizedArticleContent();
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

    //This function will summarize the article.
    protected void getSummarizedArticleContent() throws JSONException {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingTV.setText(loadText[1]);
            }
        }, 2000);

        Log.i("Status::","getSummarize Initiated");
        String URL = "https://www.summarizebot.com/scripts/demo.py";

        //String ArticleURL = article.getUrl();
        String ArticleURL =newsModel.getUrl();
        JSONObject jsonBody = new JSONObject();
        JSONObject optionObj = new JSONObject();
        jsonBody.put("text", ArticleURL);
        jsonBody.put("tab", "sm");
        optionObj.put("size","30");
        optionObj.put("domain","");
        jsonBody.put("options", optionObj);
        final String requestBody = jsonBody.toString();
        //Log.i("ReqContent>>",requestBody);

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.i("RespContent>>", response);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            JSONObject jsonObject =jsonArray.getJSONObject(0);
                            JSONArray jsonArray1 = jsonObject.getJSONArray("summary");
                            StringBuilder content= new StringBuilder();
                            for (int i=0;i<jsonArray1.length();i++){

                                JSONObject jsonObject1 =jsonArray1.getJSONObject(i);
                                String sentn = jsonObject1.getString("sentence")+"\n\n";
                                //Log.i("RespContentSentn>>", sentn);
                                content.append(sentn);
                            }
                            showArticle(content.toString());

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

    //This function will show content of the article to be shown.
    void showArticle(String content){
        contentEng=content;
        try {
            Glide.with(this)
                    .load(newsModel.getUrlToImage())
                    .into(newsImg);
            loadingTV.setText(loadText[2]);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingTV.setText(loadText[2]);
                loadingCL.setVisibility(View.GONE);
                contentCL.setVisibility(View.VISIBLE);
                newsContent.setText(content);
                newsTitle.setText(newsModel.getTitle());
            }
        }, 2000);
    }
}