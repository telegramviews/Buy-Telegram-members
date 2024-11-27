package com.insta.tele.member;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.internal.Constants;
import com.insta.tele.member.Adapters.HistoryAdapter;
import com.insta.tele.member.Adapters.ShopAdapter;
import com.insta.tele.member.Structures.HistoryStruct;
import com.insta.tele.member.Structures.InstaServicesStruct;
import com.insta.tele.member.Structures.ShopStruct;
import com.insta.tele.member.Utils.AppAssistant;
import com.insta.tele.member.Utils.TinyDB;
import com.insta.tele.member.util.IabHelper;
import com.insta.tele.member.util.IabResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.insta.tele.member.Utils.AppAssistant.FONT;

public class ShopActivity extends AppCompatActivity {

    //Initializations
    AppAssistant appAssistant;
    TinyDB tinyDB;
    ///
    RecyclerView recyclerView;
    List<ShopStruct> dataList = new ArrayList<>();


   public  Activity activity = ShopActivity.this;
   public static int RC_REQUEST = 10001;
    public static IabHelper mHelper;
    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvwAES+fRUZFc42OFbvyKCrXqVUynwTrcZj34Q3etNMzrZcvLaAMYfGZRnTS9hPJ/KZf4TJmktg7IxxpKbuhoX0iSABzQW/mpp3NxB7FU9K+P1vkFpwpz5uP1PoFfQA67MtWIAzDu18VZA0pZnkCafMbXIjiIYO0bmZ3kthXVGBmNVdAx3IjhHYondhS1WxM+fC4yK5/Kiehm2f1cBsqEyzqFSli+DnHMEJ8pUsOr3q+8fVk+vZ2spGPUFMtEisZqqAdVIH01uwAoX9IwaB/KpM3zZdu9iycA7L2JG4RHcjEMilHBzvUj2pFVSw8/YBgmyLjZhZf4mDrerXgoPt5kCwIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        appAssistant = new AppAssistant(getApplicationContext());
        tinyDB = new TinyDB(getApplicationContext());

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        dataList.clear();

        setTitle("Coin Shop");



        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Toast.makeText(activity, "Problem setting up In-app Billing: " + result, Toast.LENGTH_SHORT).show();
                    Log.e("ERROR_LOADING",result.toString());

                    AlertDialog dialog = new AlertDialog.Builder(ShopActivity.this).setMessage("Failed to setting up Google play in-App Billing services on Your devices ! please check your network connection or Update your google Play services To lastest Version !").setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            onBackPressed();
                        }
                    }).setCancelable(false).show();
                    TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                    TextView textView2 = (TextView) dialog.findViewById(android.R.id.button1);
                    textView.setTypeface(FONT);
                    textView2.setTypeface(FONT);



                } else {
                    Log.e("ITS OK","BILLING SYSTEM IS OK");
                    //Load SHOP




                    appAssistant.ShowProgressDialog(ShopActivity.this,getString(R.string.please_wait));
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://hearing-aid.ir/application/LoadShop.php",
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
                                                int shop_id = array.getJSONObject(i).getInt("shop_id");
                                                int coin_count = array.getJSONObject(i).getInt("coin_count");
                                                int coin_price = array.getJSONObject(i).getInt("coin_price");
                                                String sku = array.getJSONObject(i).getString("sku");


                                                ShopStruct data = new ShopStruct(shop_id,coin_count,coin_price,sku);
                                                dataList.add(data);

                                            }

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        appAssistant.HideProgressDialog();
                                        recyclerView.setVisibility(View.GONE);
                                        appAssistant.ShowToast(getApplicationContext(),"Error In Shop Loading ...", Toast.LENGTH_LONG);
                                    }
                                    appAssistant.HideProgressDialog();

                                    //Set
                                    ShopAdapter adapter = new ShopAdapter(dataList,ShopActivity.this);
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error)
                                {
                                    appAssistant.HideProgressDialog();
                                    appAssistant.failedNetwork(ShopActivity.this);
                                }
                            })

                    {
                        @Override
                        protected Map<String,String> getParams()
                        {
                            Map<String,String> params = new HashMap<String, String>();
                            return params;
                        }};

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(stringRequest);




                }
            }
        });



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    }

