package com.insta.tele.member;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
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

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.insta.tele.member.Utils.AppAssistant.FONT;

public class SendOrderActivity extends AppCompatActivity {

    TinyDB tinyDB;
    AppAssistant appAssistant;



    ///Important vars
    int service_count,service_coin;
    String service_name,type;


    EditText target;
    Button sendBtn;
    TextView desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_order);

        tinyDB = new TinyDB(getApplicationContext());
        appAssistant = new AppAssistant(getApplicationContext());

        service_count = getIntent().getExtras().getInt("service_count");
        service_coin = getIntent().getExtras().getInt("service_coin");
        service_name = getIntent().getExtras().getString("service_name");
        type = getIntent().getExtras().getString("type");


        Log.e("SERVICE_NAME",service_name);


        setTitle(String.valueOf(service_count + " " + service_name));


        target = (EditText)findViewById(R.id.target);
        sendBtn = (Button)findViewById(R.id.sendBtn);
        desc = (TextView)findViewById(R.id.desc);

        target.setTypeface(FONT);
        sendBtn.setTypeface(FONT);
        desc.setTypeface(FONT);



        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (target.getText().toString().equals("") || target.getText().toString().equals(null)){

                    appAssistant.ShowToast(getApplicationContext(),getString(R.string.field_completion), Toast.LENGTH_LONG);

                }else {

                    //ok send order and decrease coin from user



                    appAssistant.ShowProgressDialog(SendOrderActivity.this,getString(R.string.please_wait));
                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://hearing-aid.ir/application/SubmitOrder.php",
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response)
                                {
                                    appAssistant.HideProgressDialog();
                                    if (response.equals("failed")){
                                        appAssistant.ShowDialog(SendOrderActivity.this,getString(R.string.wrong_information));
                                    } else if (response.equals("done")){


                                        AlertDialog dialog = new AlertDialog.Builder(SendOrderActivity.this).setMessage(getString(R.string.submitted_successfully)).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        }).setCancelable(false).show();
                                        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                                        TextView textView2 = (TextView) dialog.findViewById(android.R.id.button1);
                                        textView.setTypeface(FONT);
                                        textView2.setTypeface(FONT);




                                    }

                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error)
                                {
                                    appAssistant.HideProgressDialog();
                                    appAssistant.failedNetwork(SendOrderActivity.this);
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
                            params.put("order_date",persianDate + " | " + time);
                            params.put("order_email",appAssistant.toEnglishNumber(tinyDB.getString("USER_EMAIL")));
                            params.put("order_type",appAssistant.toEnglishNumber(type));
                            params.put("order_service",appAssistant.toEnglishNumber(service_name));
                            params.put("order_target",appAssistant.toEnglishNumber(target.getText().toString()));
                            params.put("order_count",appAssistant.toEnglishNumber(String.valueOf(service_count)));
                            params.put("order_target",appAssistant.toEnglishNumber(target.getText().toString()));
                            params.put("order_status",getString(R.string.submitted));
                            params.put("service_coin",String.valueOf(service_coin));
                            return params;
                        }};

                    RequestQueue requestQueue2 = Volley.newRequestQueue(getApplicationContext());
                    stringRequest2.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue2.add(stringRequest2);








                }
            }
        });


        //set ui of target and desc
        if (type.equals("instagram")){

            if (service_name.equals("Instagram Follower")){ //KEY = Instagram Follower

                desc.setText(getString(R.string.insta_flw_desc));

            }else if (service_name.equals("Instagram Post Like")){ //KEY = Instagram Post Like

                desc.setText(getString(R.string.insta_postlike_desc));

            }else if (service_name.equals("Instagram IGTV View")){ //KEY = Instagram IGTV View

                desc.setText(getString(R.string.insta_igtvview_desc));

            }else if (service_name.equals("Instagram Video View")){ //KEY = Instagram Video View

                desc.setText(getString(R.string.insta_videoview_desc));

            }else if (service_name.equals("Instagram Story View")){ //KEY = Instagram Story View

                desc.setText(getString(R.string.insta_storyview_desc));

            }

        }else if (type.equals("telegram")){

            //telegram
            if (service_name.equals("Telegram Channel Member")){ //KEY = Telegram Channel Member

                desc.setText(getString(R.string.telegram_channel_member));


            }else if (service_name.equals("Telegram Channel View")) { //KEY = Telegram Channel View

                desc.setText(getString(R.string.telegram_channel_view));


            }else if (service_name.equals("Telegram Group Member")){ //KEY = Telegram Group Member

                desc.setText(getString(R.string.telegram_group_member));


            }



        }



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
