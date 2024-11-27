package com.insta.tele.member;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.insta.tele.member.Fragments.InstaMainFragment;
import com.insta.tele.member.Fragments.TelegramMainFragment;
import com.insta.tele.member.Utils.AppAssistant;
import com.insta.tele.member.Utils.CustomTypefaceSpan;
import com.insta.tele.member.Utils.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import static com.insta.tele.member.Utils.AppAssistant.FONT;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    BottomNavigationView bottomNavigationView;

    NavigationView navigationView;
    AppAssistant appAssistant;
    TinyDB tinyDB;
    public static FragmentTransaction transaction;
    Fragment selectedFragment = InstaMainFragment.newInstance();

    public static final String GOOGLE_ACCOUNT = "google_account";


    public static TextView coin_value;

    public static TextView toolbar_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.app_name));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        toolbar_title =(TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setTypeface(appAssistant.FONT);



        appAssistant = new AppAssistant(getApplicationContext());
        tinyDB = new TinyDB(getApplicationContext());
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        coin_value = (TextView)findViewById(R.id.coin_value);
        coin_value.setTypeface(appAssistant.FONT);
        coin_value.setText(String.valueOf(tinyDB.getInt("USER_COIN")));

        toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);




        bottomNavigationView  = (BottomNavigationView)
                findViewById(R.id.navigation);
         navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                        switch (item.getItemId()) {
                            case R.id.bottom_insta:
                                selectedFragment = InstaMainFragment.newInstance();
                                break;
                            case R.id.bottom_telegram:
                                selectedFragment = TelegramMainFragment.newInstance();
                                break;

                        }
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //Def Loaded Fragment
        transaction.replace(R.id.frame_layout, InstaMainFragment.newInstance());
        transaction.commit();
        setNavigationBottomInfo();
        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
        bottomNavigationView.setItemIconTintList(null);


        //Read User Coin Online ( Read User All Data Online )

        appAssistant.ShowProgressDialog(MainActivity.this,getString(R.string.please_wait));
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://hearing-aid.ir/application/ReadCoinOnline.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        appAssistant.HideProgressDialog();
                        if (response.equals("failed")){
                            appAssistant.ShowDialog(MainActivity.this,getString(R.string.wrong_information));
                        }else{
                            Log.e("LOGIN_JSON",response.toString());
                            try {
                                JSONArray array = new JSONArray(response.toString());
                                tinyDB.putString("IsLogged","True");
                                tinyDB.putString("USER_ID",String.valueOf(array.getJSONObject(0).getInt("id")));
                                tinyDB.putString("USER_EMAIL",array.getJSONObject(0).getString("user_email"));
                                tinyDB.putString("USER_PASSWORD",array.getJSONObject(0).getString("user_password"));
                                tinyDB.putString("USER_FULLNAME",array.getJSONObject(0).getString("user_fullname"));
                                tinyDB.putString("USER_PHONE",array.getJSONObject(0).getString("user_phone"));
                                tinyDB.putInt("USER_COIN",array.getJSONObject(0).getInt("user_coin"));


                                //set Coin On TextView

                                coin_value.setText(String.valueOf(array.getJSONObject(0).getInt("user_coin")));



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
                        appAssistant.failedNetwork(MainActivity.this);
                    }
                })

        {
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",tinyDB.getString("USER_EMAIL"));
                return params;
            }};

        RequestQueue requestQueue2 = Volley.newRequestQueue(getApplicationContext());
        stringRequest2.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue2.add(stringRequest2);

        setNavigationInfo();


    }


    public void setNavigationInfo(){
        View hView =  navigationView.getHeaderView(0);
        TextView menu_name = (TextView)hView.findViewById(R.id.menu_name);
        if (tinyDB.getString("IsLogged").equals("True")){
            menu_name.setText(tinyDB.getString("USER_FULLNAME"));
        }
        menu_name.setTypeface(FONT, Typeface.BOLD);
        TextView menu_email = (TextView)hView.findViewById(R.id.menu_email);
        menu_email.setTypeface(appAssistant.FONT);
        if (tinyDB.getString("IsLogged").equals("True")){

            menu_email.setText(tinyDB.getString("USER_EMAIL"));

        }else {
            menu_email.setVisibility(View.GONE);
        }



        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToDrawer(subMenuItem);
                }
            }
            applyFontToDrawer(mi);
        }
    }

    private void applyFontToDrawer(MenuItem mi) {
        View hView =  navigationView.getHeaderView(0);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , FONT), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public void setNavigationBottomInfo(){
        Menu m = bottomNavigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToNaviBottom(subMenuItem);
                }
            }
            applyFontToNaviBottom(mi);
        }
    }

    private void applyFontToNaviBottom(MenuItem mi) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , FONT), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id==R.id.nav_shop){

            //Go to shop
            Intent intent = new Intent(getApplicationContext(),ShopActivity.class);
            startActivity(intent);

        }else if (id==R.id.nav_history){

            //history
            Intent intent = new Intent(getApplicationContext(),HistoryActivity.class);
            startActivity(intent);


        } else if (id==R.id.nav_rate){

            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }


        }else if (id == R.id.nav_about) {

            appAssistant.ShowDialog(MainActivity.this,getString(R.string.about_text));

        } else if (id == R.id.nav_exit) {

            finish();
            System.exit(0);

        } else if (id == R.id.nav_logout) {


            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setMessage(getString(R.string.logout_text)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    if (tinyDB.getString("GOOGLE_SIGN").equals("True")){

                        //Az Google Login Shode Bood
                        GoogleSignInClient googleSignInClient;
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getResources().getString(R.string.web_client_id))
                                .build();
                        googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);


                        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //On Succesfull signout we navigate the user back to LoginActivity
                                tinyDB.clear();
                                Intent intent = new Intent(getApplicationContext(),FirstActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        });


                    }else {

                        //Mamooli Login Shode Bood
                        tinyDB.clear();
                        Intent intent = new Intent(getApplicationContext(),FirstActivity.class);
                        startActivity(intent);
                        finish();


                    }


                }
            }).setCancelable(false).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {



                }
            }).show();
            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            TextView textView2 = (TextView) dialog.findViewById(android.R.id.button1);
            TextView textView3 = (TextView) dialog.findViewById(android.R.id.button2);
            textView.setTypeface(FONT);
            textView2.setTypeface(FONT);
            textView3.setTypeface(FONT);




        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
