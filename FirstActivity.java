package com.insta.tele.member;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.insta.tele.member.Utils.AppAssistant;
import com.insta.tele.member.Utils.TinyDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FirstActivity extends AppCompatActivity {



    //Android 6 Runtime Permissions
    public static final int MULTIPLE_PERMISSIONS = 10;
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_NETWORK_STATE};


    AppAssistant appAssistant;
    TinyDB tinyDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first);


        appAssistant = new AppAssistant(getApplicationContext());
        tinyDB = new TinyDB(getApplicationContext());
        checkPermissions();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tinyDB.getString("IsLogged").equals("True")){
                    //Login Shode va Boro Activity Asli
                    Intent start = new Intent(FirstActivity.this, MainActivity.class);
                    startActivity(start);
                    finish();

                }else {
                    //Login Nashode Boro page login ya register
                    Intent start = new Intent(FirstActivity.this, LoginActivity.class);
                    startActivity(start);
                    finish();
                }
            }
        }, 5000);



    }


    private boolean checkPermissions() {
        int result;
        final List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(FirstActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(FirstActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    appAssistant.ShowToast(getApplicationContext(), "All permissions granted .", Toast.LENGTH_LONG);
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else {
                    appAssistant.ShowToast(getApplicationContext(), "Permissions not granted !", Toast.LENGTH_LONG);
                    checkPermissions();
                }
                return;
            }
        }
    }

}
