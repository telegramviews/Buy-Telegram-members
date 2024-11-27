package com.insta.tele.member;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.insta.tele.member.Adapters.HistoryAdapter;
import com.insta.tele.member.Structures.HistoryStruct;
import com.insta.tele.member.Structures.InstaServicesStruct;
import com.insta.tele.member.Utils.AppAssistant;
import com.insta.tele.member.Utils.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {


    AppAssistant appAssistant;
    TinyDB tinyDB;


    TextView no_media;

    RecyclerView recyclerView;
    List<HistoryStruct> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        appAssistant = new AppAssistant(getApplicationContext());
        tinyDB = new TinyDB(getApplicationContext());

        no_media = (TextView)findViewById(R.id.no_media);
        no_media.setTypeface(appAssistant.FONT);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        dataList.clear();

        setTitle("Order History");

        appAssistant.ShowProgressDialog(HistoryActivity.this,getString(R.string.please_wait));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://hearing-aid.ir/application/History.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.e("RESPE_MEDIA",response.toString());
                        try {
                            {
                                JSONArray array = new JSONArray(response);
                                for (int i = 0; i < array.length() ; i++) {
                                    int order_id = array.getJSONObject(i).getInt("order_id");
                                    String order_email = array.getJSONObject(i).getString("order_email");
                                    String order_type = array.getJSONObject(i).getString("order_type");
                                    String order_service = array.getJSONObject(i).getString("order_service");
                                    String order_target = array.getJSONObject(i).getString("order_target");
                                    String order_count = array.getJSONObject(i).getString("order_count");
                                    String order_status = array.getJSONObject(i).getString("order_status");


                                    HistoryStruct data = new HistoryStruct(order_id,order_email,order_type,order_service,order_target,order_count,order_status);
                                    dataList.add(data);

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            appAssistant.HideProgressDialog();
                            no_media.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        appAssistant.HideProgressDialog();
                        //Set
                        HistoryAdapter adapter = new HistoryAdapter(dataList);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        appAssistant.HideProgressDialog();
                        appAssistant.failedNetwork(HistoryActivity.this);
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

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);




    }
}
