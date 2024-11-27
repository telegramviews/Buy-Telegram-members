package com.insta.tele.member;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.insta.tele.member.Utils.AppAssistant;
import com.insta.tele.member.Utils.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import static com.insta.tele.member.MainActivity.GOOGLE_ACCOUNT;
import static com.insta.tele.member.Utils.AppAssistant.FONT;

public class LoginActivity extends AppCompatActivity {

    AppAssistant appAssistant;
    TinyDB tinyDB;


    ///

    TextView login_title;
    EditText edtUser,edtPsw;
    Button loginBtn,loginGoogle;
    TextView exitTxt;


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
                        // be available.
                        Log.d(TAG, "onConnectionFailed:" + connectionResult);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        appAssistant = new AppAssistant(getBaseContext());
        tinyDB = new TinyDB(getApplicationContext());

        if (tinyDB.getString("IsLogged").equals("True")){
            //User Logged in Before This , now go To Next Activity
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();

        }





        login_title = (TextView)findViewById(R.id.login_title);
        edtUser = (EditText)findViewById(R.id.edtUser);
        edtPsw = (EditText)findViewById(R.id.edtPsw);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        exitTxt = (TextView)findViewById(R.id.exitTxt);
        loginGoogle = (Button)findViewById(R.id.loginGoogle);


        loginGoogle.setTypeface(FONT);
        login_title.setTypeface(FONT, Typeface.BOLD);
        edtUser.setTypeface(FONT);
        edtPsw.setTypeface(FONT);
        loginBtn.setTypeface(FONT);
        exitTxt.setTypeface(FONT,Typeface.BOLD);

        TextInputLayout tl1 = (TextInputLayout)findViewById(R.id.tl1);
        tl1.setTypeface(FONT);
        TextInputLayout tl2 = (TextInputLayout)findViewById(R.id.tl2);
        tl2.setTypeface(FONT);

        exitTxt.setPaintFlags(exitTxt.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        exitTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);


            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //mikhad mamoli login kone

                if (appAssistant.isNetworkAvailable(getApplicationContext()) == true){

                    if (edtPsw.getText().toString().equals("") || edtUser.getText().toString().equals("")){
                        appAssistant.ShowToast(getApplicationContext(),getString(R.string.field_completion), Toast.LENGTH_LONG);
                    }else {
                        //Ok
                        appAssistant.ShowProgressDialog(LoginActivity.this,getString(R.string.please_wait));
                        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://hearing-aid.ir/application/Login.php",
                                new Response.Listener<String>()
                                {
                                    @Override
                                    public void onResponse(String response)
                                    {
                                        appAssistant.HideProgressDialog();
                                        if (response.equals("failed")){
                                            appAssistant.ShowDialog(LoginActivity.this,getString(R.string.wrong_information));
                                        }else{
                                            Log.e("LOGIN_JSON",response.toString());
                                            try {
                                                JSONArray array = new JSONArray(response.toString());

                                                String user_type = array.getJSONObject(0).getString("user_type");

                                                if (user_type.equals("normal")){

                                                    //ok va loginesh ro ba ramz chek kon hala



                                                    appAssistant.ShowProgressDialog(LoginActivity.this,getString(R.string.please_wait));
                                                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://hearing-aid.ir/application/Login2.php",
                                                            new Response.Listener<String>()
                                                            {
                                                                @Override
                                                                public void onResponse(String response)
                                                                {
                                                                    appAssistant.HideProgressDialog();
                                                                    if (response.equals("failed")){
                                                                        appAssistant.ShowDialog(LoginActivity.this,getString(R.string.wrong_information));
                                                                    }else{
                                                                        Log.e("LOGIN_JSON",response.toString());
                                                                        try {
                                                                            JSONArray array = new JSONArray(response.toString());
                                                                            tinyDB.putString("USER_ID",String.valueOf(array.getJSONObject(0).getInt("id")));
                                                                            tinyDB.putString("IsLogged","True");
                                                                            tinyDB.putString("USER_ID",String.valueOf(array.getJSONObject(0).getInt("id")));
                                                                            tinyDB.putString("USER_EMAIL",array.getJSONObject(0).getString("user_email"));
                                                                            tinyDB.putString("USER_PASSWORD",array.getJSONObject(0).getString("user_password"));
                                                                            tinyDB.putString("USER_FULLNAME",array.getJSONObject(0).getString("user_fullname"));
                                                                            tinyDB.putString("USER_PHONE",array.getJSONObject(0).getString("user_phone"));
                                                                            tinyDB.putString("USER_TYPE",array.getJSONObject(0).getString("user_type"));
                                                                            tinyDB.putInt("USER_COIN",array.getJSONObject(0).getInt("user_coin"));

                                                                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                                                            startActivity(intent);
                                                                            finish();









                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }


                                                                    }

                                                                }
                                                            },
                                                            new Response.ErrorListener()
                                                            {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error)
                                                                {
                                                                    appAssistant.HideProgressDialog();
                                                                    appAssistant.failedNetwork(LoginActivity.this);
                                                                }
                                                            })

                                                    {
                                                        @Override
                                                        protected Map<String,String> getParams()
                                                        {
                                                            Map<String,String> params = new HashMap<String, String>();
                                                            params.put("email",appAssistant.toEnglishNumber(edtUser.getText().toString()));
                                                            params.put("password",appAssistant.toEnglishNumber(edtPsw.getText().toString()));
                                                            return params;
                                                        }};

                                                    RequestQueue requestQueue2 = Volley.newRequestQueue(getApplicationContext());
                                                    stringRequest2.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                                    requestQueue2.add(stringRequest2);




                                                }else {
                                                    appAssistant.ShowDialog(LoginActivity.this,getString(R.string.wrong_information));
                                                }







                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                        }

                                    }
                                },
                                new Response.ErrorListener()
                                {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        appAssistant.HideProgressDialog();
                                        appAssistant.failedNetwork(LoginActivity.this);
                                    }
                                })

                        {
                            @Override
                            protected Map<String,String> getParams()
                            {
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("email",appAssistant.toEnglishNumber(edtUser.getText().toString()));
                                params.put("password",appAssistant.toEnglishNumber(edtPsw.getText().toString()));
                                return params;
                            }};

                        RequestQueue requestQueue2 = Volley.newRequestQueue(getApplicationContext());
                        stringRequest2.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requestQueue2.add(stringRequest2);




                    }



                }else {

                    appAssistant.failedNetwork(LoginActivity.this);

                }



            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {

                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();

            final String email = acct.getEmail();

            Log.e(TAG, "Name: " + personName + ", email: " + email);



            Log.e("LOGGED_EMAIL",email);

            appAssistant.ShowProgressDialog(LoginActivity.this,getString(R.string.please_wait));
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://hearing-aid.ir/application/CheckGoogleLogin.php",
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            appAssistant.HideProgressDialog();
                            if (response.equals("failed")){
                                //No Registered Email found with google login

                                AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this).setMessage("Login authorization with google account has Done ! now you should complete your profile information for first time to use !").setPositiveButton(getString(R.string.register), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //Go to the register and hide field email va ramz vase gmail
                                        Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                                        intent.putExtra("user_type","gmail");
                                        intent.putExtra("user_email",email);
                                        intent.putExtra("user_password","");
                                        startActivity(intent);





                                    }
                                }).setCancelable(false).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //if user canceled , then logout from google account

                                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                                new ResultCallback<Status>() {
                                                    @Override
                                                    public void onResult(Status status) {
                                                        tinyDB.clear();
                                                        Intent intent = new Intent(LoginActivity.this, FirstActivity.class);
                                                        startActivity(intent);
                                                        finish();

                                                    }
                                                });


                                    }
                                }).show();
                                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                                TextView textView2 = (TextView) dialog.findViewById(android.R.id.button1);
                                TextView textView3 = (TextView) dialog.findViewById(android.R.id.button2);
                                textView.setTypeface(FONT);
                                textView2.setTypeface(FONT);
                                textView3.setTypeface(FONT);




                            }else{
                                Log.e("LOGIN_JSON",response.toString());
                                try {
                                    JSONArray array = new JSONArray(response.toString());
                                    tinyDB.putString("IsLogged","True");
                                    tinyDB.putString("USER_ID",String.valueOf(array.getJSONObject(0).getInt("id")));
                                    tinyDB.putString("USER_EMAIL",array.getJSONObject(0).getString("user_email"));
                                    tinyDB.putString("USER_TYPE",array.getJSONObject(0).getString("user_type"));
                                    tinyDB.putString("USER_FULLNAME",array.getJSONObject(0).getString("user_fullname"));
                                    tinyDB.putString("USER_PHONE",array.getJSONObject(0).getString("user_phone"));
                                    tinyDB.putInt("USER_COIN",array.getJSONObject(0).getInt("user_coin"));



                                    tinyDB.putString("GOOGLE_SIGN","True");


                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra(GOOGLE_ACCOUNT, acct);

                                    startActivity(intent);
                                    finish();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            appAssistant.HideProgressDialog();
                            appAssistant.failedNetwork(LoginActivity.this);
                        }
                    })

            {
                @Override
                protected Map<String,String> getParams()
                {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("email",email);
                    return params;
                }};

            RequestQueue requestQueue2 = Volley.newRequestQueue(getApplicationContext());
            stringRequest2.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue2.add(stringRequest2);





        } else {
            // Signed out, show unauthenticated UI.


        }
    }





}
