package com.project.shortpress.Fragments.FirstTime;

import static android.content.ContentValues.TAG;

import static com.project.shortpress.Config.URL.doLogin_URL;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.project.shortpress.Activities.GetPreferenceActivity;
import com.project.shortpress.R;
import com.project.shortpress.Config.SharedPrefFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FirstTimeFragment3 extends Fragment {
    private static final int RC_SIGN_IN =1;
    Button setGoogleButton;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    RequestQueue requestQueue;
    SharedPrefFunctions sharedPrefFunctions;


    private  void setUser(int uid, String displayName, String email, String photoURL, String joinDate){
        sharedPrefFunctions.setUser(uid,displayName,email,photoURL,joinDate);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.googleAccountWebClientID))
                .requestEmail()
                .build();

        sharedPrefFunctions=new SharedPrefFunctions(getActivity());
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        //updateUI(account);

        requestQueue = Volley.newRequestQueue(getActivity());

        View view = inflater.inflate(R.layout.fragment_first_time3, container, false);
        setGoogleButton = (Button) view.findViewById(R.id.gmailButton);
        setGoogleButton.setOnClickListener(view1 -> {
            signIn();
        });

        return view;
    }

    protected void proceedToPref(){
            Intent i = new Intent(getActivity(), GetPreferenceActivity.class);
            i.putExtra("act","First");
            startActivity(i);
            getActivity().finish();
    }

    protected void doLogin(GoogleSignInAccount account) throws JSONException {
        Log.i("Status::","getSummarize Initiated");

        //String ArticleURL = article.getUrl();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("uname", account.getDisplayName());
        jsonBody.put("uemail", account.getEmail());
        jsonBody.put("uPhotoURL", account.getPhotoUrl());
        final String requestBody = jsonBody.toString();
        Log.i("ReqContent>>",requestBody);

        StringRequest request = new StringRequest(Request.Method.POST, doLogin_URL,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.i("RespContent>>", response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            int uid = jsonObject.getInt("uid");
                            String joinDate = jsonObject.getString("joinDate");
                            setUser(uid,account.getDisplayName(),account.getEmail(),account.getPhotoUrl().toString(),joinDate);
                            proceedToPref();
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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account==null){
            Toast.makeText(getActivity(),"Google Signup Failed",Toast.LENGTH_LONG).show();
        }
        else {
            try {
                doLogin(account);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

}