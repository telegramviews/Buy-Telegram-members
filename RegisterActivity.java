package com.insta.tele.member;

import android.content.Intent;
import android.graphics.Typeface;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
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
import com.insta.tele.member.Utils.AppAssistant;
import com.insta.tele.member.Utils.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.GONE;
import static com.insta.tele.member.Utils.AppAssistant.FONT;

public class RegisterActivity extends AppCompatActivity {

    AppAssistant appAssistant;
    TinyDB tinyDB;

    TextView login_title;
    EditText edtUser,edtPsw,edtFullname,edtPhone;
    Button loginBtn;



    String user_type = "";
    String user_email = "";
    String user_password = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);


        appAssistant = new AppAssistant(getBaseContext());
        tinyDB = new TinyDB(getApplicationContext());


        login_title = (TextView)findViewById(R.id.login_title);
        edtUser = (EditText)findViewById(R.id.edtUser);
        edtPsw = (EditText)findViewById(R.id.edtPsw);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        edtFullname = (EditText)findViewById(R.id.edtFullname);
        edtPhone = (EditText)findViewById(R.id.edtPhone);



        login_title.setTypeface(FONT, Typeface.BOLD);
        edtUser.setTypeface(FONT);
        edtPsw.setTypeface(FONT);
        loginBtn.setTypeface(FONT);
        edtPhone.setTypeface(appAssistant.FONT);
        edtFullname.setTypeface(appAssistant.FONT);

        TextInputLayout tl1 = (TextInputLayout)findViewById(R.id.tl1);
        tl1.setTypeface(FONT);
        TextInputLayout tl2 = (TextInputLayout)findViewById(R.id.tl2);
        tl2.setTypeface(FONT);
        TextInputLayout tl3 = (TextInputLayout)findViewById(R.id.tl2);
        tl3.setTypeface(FONT);
        TextInputLayout tl4 = (TextInputLayout)findViewById(R.id.tl2);
        tl4.setTypeface(FONT);


        try{
            user_email = getIntent().getExtras().getString("user_email");
            user_password = getIntent().getExtras().getString("user_password");
            user_type = getIntent().getExtras().getString("user_type");


            if (user_type.equals("gmail")){
                tl1.setVisibility(GONE);
                tl2.setVisibility(GONE);
                edtUser.setVisibility(GONE);
                edtPsw.setVisibility(GONE);
            }


        }catch (Exception e){
            e.printStackTrace();
        }




        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (user_type.equals("") || user_type.equals(null)){

                    //register mamoli
                    if (edtUser.getText().toString().equals("") || edtFullname.getText().toString().equals("") || edtPhone.getText().toString().equals("") || edtPsw.getText().toString().equals("")){
                        appAssistant.ShowToast(getApplicationContext(),getString(R.string.field_completion), Toast.LENGTH_LONG);

                    }else {


                        if (isEmailValid(edtUser.getText().toString())==true){

                            //Ok
                            appAssistant.ShowProgressDialog(RegisterActivity.this,getString(R.string.please_wait));
                            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://hearing-aid.ir/application/Register.php",
                                    new Response.Listener<String>()
                                    {
                                        @Override
                                        public void onResponse(String response)
                                        {
                                            appAssistant.HideProgressDialog();
                                            if (response.equals("failed")){
                                                appAssistant.ShowDialog(RegisterActivity.this,getString(R.string.wrong_information));
                                            }else if (response.equals("exists")){
                                                appAssistant.ShowDialog(RegisterActivity.this,getString(R.string.user_exists));
                                                edtUser.setText("");
                                                edtFullname.setText("");
                                                edtPhone.setText("");
                                                edtPsw.setText("");

                                            } else if (response.equals("done")){

                                                Log.e("Register_JSON",response.toString());

                                                tinyDB.putString("IsLogged","True");
                                                //tinyDB.putString("USER_ID",String.valueOf(array.getJSONObject(0).getInt("id")));
                                                tinyDB.putString("USER_EMAIL",edtUser.getText().toString());
                                                tinyDB.putString("USER_FULLNAME",edtFullname.getText().toString());
                                                tinyDB.putString("USER_PHONE",edtPhone.getText().toString());
                                                //Coin 0 Vase Usere taze register shode
                                                tinyDB.putInt("USER_COIN",0);

                                                if (user_type.equals("") || user_type.equals(null)){

                                                    //registere mamolie ba ramz o ina

                                                    tinyDB.putString("USER_PASSWORD",edtPsw.getText().toString());
                                                    tinyDB.putString("USER_TYPE","normal");

                                                }else if (user_type.equals("gmail")){
                                                    tinyDB.putString("GOOGLE_SIGN","True");
                                                    tinyDB.putString("USER_PASSWORD","");
                                                    tinyDB.putString("USER_TYPE","gmail");
                                                }

                                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(intent);
                                                finish();





                                            }

                                        }
                                    },
                                    new Response.ErrorListener()
                                    {
                                        @Override
                                        public void onErrorResponse(VolleyError error)
                                        {
                                            appAssistant.HideProgressDialog();
                                            appAssistant.failedNetwork(RegisterActivity.this);
                                        }
                                    })

                            {
                                @Override
                                protected Map<String,String> getParams()
                                {
                                    Map<String,String> params = new HashMap<String, String>();
                                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                    int minute = calendar.get(Calendar.MINUTE);
                                    int second = calendar.get(Calendar.SECOND);
                                    String time = String.valueOf(hour + ":" + minute);
                                    Time today = new Time(Time.getCurrentTimezone());
                                    today.setToNow();
                                    int Day = (today.monthDay); // Day of the month (0-31)
                                    int Month = (today.month); // Month (0-11)
                                    int Year = (today.year); // Year
                                   String persianDate = GregorianToJalali(Year, Month+1, Day);
                                    params.put("register_date",persianDate + " | " + time);
                                    params.put("email",appAssistant.toEnglishNumber(edtUser.getText().toString()));
                                    params.put("phone",appAssistant.toEnglishNumber(edtPhone.getText().toString()));
                                    params.put("fullname",appAssistant.toEnglishNumber(edtFullname.getText().toString()));
                                    if (user_type.equals("") || user_type.equals(null)){
                                        //register mamoli ba ramz
                                        params.put("password",appAssistant.toEnglishNumber(edtPsw.getText().toString()));
                                        params.put("user_type","normal");

                                    }else if (user_type.equals("gmail")){

                                        //register ba gmail
                                        params.put("password","");
                                        params.put("user_type","gmail");

                                    }
                                    return params;
                                }};

                            RequestQueue requestQueue2 = Volley.newRequestQueue(getApplicationContext());
                            stringRequest2.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            requestQueue2.add(stringRequest2);


                        }else {

                            appAssistant.ShowToast(getApplicationContext(),"Please enter valid email address !",Toast.LENGTH_LONG);

                        }




                    }


                }else {
                    //register ba gmail

                    if (edtFullname.getText().toString().equals("") || edtPhone.getText().toString().equals("")){
                        appAssistant.ShowToast(getApplicationContext(),getString(R.string.field_completion), Toast.LENGTH_LONG);

                    }else {

                        //Ok
                        appAssistant.ShowProgressDialog(RegisterActivity.this,getString(R.string.please_wait));
                        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://hearing-aid.ir/application/Register.php",
                                new Response.Listener<String>()
                                {
                                    @Override
                                    public void onResponse(String response)
                                    {
                                        appAssistant.HideProgressDialog();
                                        if (response.equals("failed")){
                                            appAssistant.ShowDialog(RegisterActivity.this,getString(R.string.wrong_information));
                                        }else if (response.equals("exists")){
                                            appAssistant.ShowDialog(RegisterActivity.this,getString(R.string.user_exists));
                                            edtUser.setText("");
                                            edtFullname.setText("");
                                            edtPhone.setText("");
                                            edtPsw.setText("");

                                        } else if (response.equals("done")){

                                            Log.e("Register_JSON",response.toString());

                                            tinyDB.putString("IsLogged","True");
                                            //tinyDB.putString("USER_ID",String.valueOf(array.getJSONObject(0).getInt("id")));
                                            tinyDB.putString("USER_FULLNAME",edtFullname.getText().toString());
                                            tinyDB.putString("USER_PHONE",edtPhone.getText().toString());
                                            //Coin 0 Vase Usere taze register shode
                                            tinyDB.putInt("USER_COIN",0);
                                            tinyDB.putString("GOOGLE_SIGN","True");

                                            if (user_type.equals("") || user_type.equals(null)){

                                                //registere mamolie ba ramz o ina
                                                tinyDB.putString("USER_EMAIL",edtUser.getText().toString());
                                                tinyDB.putString("USER_PASSWORD",edtPsw.getText().toString());
                                                tinyDB.putString("USER_TYPE","normal");

                                            }else if (user_type.equals("gmail")){
                                                tinyDB.putString("USER_EMAIL",user_email);
                                                tinyDB.putString("USER_PASSWORD","");
                                                tinyDB.putString("USER_TYPE","gmail");
                                            }

                                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                            startActivity(intent);
                                            finish();





                                        }

                                    }
                                },
                                new Response.ErrorListener()
                                {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        appAssistant.HideProgressDialog();
                                        appAssistant.failedNetwork(RegisterActivity.this);
                                    }
                                })

                        {
                            @Override
                            protected Map<String,String> getParams()
                            {
                                Map<String,String> params = new HashMap<String, String>();
                                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);
                                int second = calendar.get(Calendar.SECOND);
                                String time = String.valueOf(hour + ":" + minute);
                                Time today = new Time(Time.getCurrentTimezone());
                                today.setToNow();
                                int Day = (today.monthDay); // Day of the month (0-31)
                                int Month = (today.month); // Month (0-11)
                                int Year = (today.year); // Year
                                String persianDate = GregorianToJalali(Year, Month+1, Day);
                                params.put("register_date",persianDate + " | " + time);
                                params.put("phone",appAssistant.toEnglishNumber(edtPhone.getText().toString()));
                                params.put("fullname",appAssistant.toEnglishNumber(edtFullname.getText().toString()));
                                if (user_type.equals("") || user_type.equals(null)){
                                    //register mamoli ba ramz
                                    params.put("password",appAssistant.toEnglishNumber(edtPsw.getText().toString()));
                                    params.put("user_type","normal");
                                    params.put("email",appAssistant.toEnglishNumber(edtUser.getText().toString()));
                                }else if (user_type.equals("gmail")){

                                    //register ba gmail
                                    params.put("password","");
                                    params.put("user_type","gmail");
                                    params.put("email",user_email);

                                }
                                return params;
                            }};

                        RequestQueue requestQueue2 = Volley.newRequestQueue(getApplicationContext());
                        stringRequest2.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requestQueue2.add(stringRequest2);




                    }

                }



            }
        });

    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private String GregorianToJalali(int g_y, int g_m, int g_d)
    {

        int[] g_days_in_month = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int[] j_days_in_month = {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};

        int gy = g_y-1600;
        int gm = g_m-1;
        int gd = g_d-1;

        int g_day_no = 365*gy+div(gy+3,4)-div(gy+99,100)+div(gy+399,400);

        for (int i=0; i < gm; ++i)
            g_day_no += g_days_in_month[i];
        if (gm>1 && ((gy%4==0 && gy%100!=0) || (gy%400==0)))
            // leap and after Feb
            g_day_no++;
        g_day_no += gd;

        int j_day_no = g_day_no-79;

        int j_np = div(j_day_no, 12053); //12053 = 365*33 + 32/4
        j_day_no = j_day_no % 12053;

        int jy = 979+33*j_np+4*div(j_day_no,1461); // 1461 = 365*4 + 4/4

        j_day_no %= 1461;

        if (j_day_no >= 366) {
            jy += div(j_day_no-1, 365);
            j_day_no = (j_day_no-1)%365;
        }

        int j;
        for (j=0; j < 11 && j_day_no >= j_days_in_month[j]; ++j)
            j_day_no -= j_days_in_month[j];
        int jm = j+1;
        int jd = j_day_no+1;


        String Result= jy+"/"+jm+"/"+jd;

        return (Result);
    }


    private int div(float a, float b)
    {
        return (int)(a/b);
    }




}
