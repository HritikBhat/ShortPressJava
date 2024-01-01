package com.project.shortpress.Activities;

import static com.project.shortpress.Config.URL.getPrefs_URL;
import static com.project.shortpress.Config.URL.setPref_URL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.project.shortpress.R;
import com.project.shortpress.Config.SharedPrefFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetPreferenceActivity extends AppCompatActivity {
    Button proceed;
    HashMap<Chip,String> chips;
    ArrayList<String> selectedChips;
    SharedPrefFunctions sharedPrefFunctions;
    String uid,act;
    RequestQueue requestQueue;
    Intent intent;
    Bundle extras;

    //Selects any category if user already added preference before
    protected void preSelectChip(){
        for (Chip chip : chips.keySet()) {
            String chipName = chips.get(chip);
            if(selectedChips.contains(chipName)){
                chip.setChecked(true);
            }
        }
        selectedChips.clear();

    }

    protected void proceedToMain(){
        Intent i = new Intent(GetPreferenceActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    //To retrieve user preferences/category selected
    protected void getPref() throws JSONException {
        Log.i("Status::","getSummarize Initiated");

        //String ArticleURL = article.getUrl();
        JSONObject jsonBody = new JSONObject();
        JSONArray tnameArr = new JSONArray();
        for (String chip:selectedChips) {
            tnameArr.put(chip);
        }
        jsonBody.put("uid",uid);
        final String requestBody = jsonBody.toString();
        Log.i("ReqContent>>",requestBody);

        StringRequest request = new StringRequest(Request.Method.POST, getPrefs_URL,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.i("RespContent>>", response);

                        try {
                            if (!response.contains("Empty")) {
                                JSONArray jsonArray = new JSONArray(response);
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        selectedChips.add(jsonArray.getJSONObject(i).getString("tname"));
                                    }
                                    preSelectChip();
                                }
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

    //To set selected user preferences/category to the server
    protected void setPref() throws JSONException {
        Log.i("Status::","getSummarize Initiated");

        //String ArticleURL = article.getUrl();
        JSONObject jsonBody = new JSONObject();
        JSONArray tnameArr = new JSONArray();
        for (String chip:selectedChips) {
            tnameArr.put(chip);
        }
        if(tnameArr.length()<3){
            Toast.makeText(this, "Minimum 3 Categories to be selected!", Toast.LENGTH_SHORT).show();
        }else{
            jsonBody.put("uid",uid);
            jsonBody.put("tnameArr", tnameArr);
            final String requestBody = jsonBody.toString();
            Log.i("ReqContent>>",requestBody);

            StringRequest request = new StringRequest(Request.Method.POST, setPref_URL,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            Log.i("RespContent>>", response);
                            if (response.contains("Success")){

                                if (act.contains("Profile")){
                                    Toast.makeText(GetPreferenceActivity.this, "Preference Set Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    proceedToMain();
                                }

                            }
                            else{
                                Toast.makeText(GetPreferenceActivity.this, "Something gone wrong", Toast.LENGTH_SHORT).show();
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

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_preference);

        intent = getIntent();
        extras = intent.getExtras();
        act= intent.getStringExtra("act");

        sharedPrefFunctions = new SharedPrefFunctions(this);
        uid = String.valueOf(sharedPrefFunctions.getUserId());
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        selectedChips = new ArrayList<>();
        chips=new HashMap<>();
        chips.put((Chip)findViewById(R.id.chipPolitics),"Politics");
        chips.put((Chip)findViewById(R.id.chipBusiness),"Business");
        chips.put((Chip)findViewById(R.id.chipScience),"Science");
        chips.put((Chip)findViewById(R.id.chipCelebrity),"Celebrity");
        chips.put((Chip)findViewById(R.id.chipHealth),"Health");
        chips.put((Chip)findViewById(R.id.chipTravel),"Travel");
        chips.put((Chip)findViewById(R.id.chipTechnology),"Technology");
        chips.put((Chip)findViewById(R.id.chipSports),"Sports");

        try {
            getPref();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        proceed = findViewById(R.id.letsgobutton);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (Chip chip : chips.keySet()) {
                    if (chip.isChecked()){
                        selectedChips.add(chips.get(chip));
                    }
                }
                try {
                    setPref();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }
}