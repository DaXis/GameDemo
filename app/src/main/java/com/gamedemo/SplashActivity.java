package com.gamedemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import com.gamedemo.objs.UserObj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends Activity {

    private final int DURACION_SPLASH = 3000, MY_WRITE_EXTERNAL_STORAGE = 13, MY_READ_EXTERNAL_STORAGE = 14;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        checkLogin();
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M)
            checkWritePermission();
        else
            initActivity();
    }

    private void checkLogin(){
        if(SingleTon.getSettings().getBoolean("login", false)){
            parseLogin(SingleTon.getSettings().getString("json_login",""));
        }
    }

    public void parseLogin(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject.getString("status").equals("ok")){
                UserObj userObj = new UserObj();
                userObj.id = jsonObject.getInt("id");
                userObj.email = jsonObject.getString("email");
                userObj.name = jsonObject.getString("name");
                userObj.ap = jsonObject.getString("ap");
                userObj.am = jsonObject.getString("am");
                userObj.alias = jsonObject.getString("alias");
                userObj.phone = jsonObject.getString("phone");
                userObj.image = jsonObject.getString("image");
                userObj.xboxGT = jsonObject.getString("xboxGT");
                userObj.psn = jsonObject.getString("psn");
                userObj.fc = jsonObject.getString("fc");

                SingleTon.getEditor().putBoolean("login", true);
                SingleTon.getEditor().putString("json_login",json);
                SingleTon.getEditor().commit();

                SingleTon.setUserObj(userObj);
            } else
                SingleTon.setUserObj(null);
        } catch (JSONException e) {
            e.printStackTrace();
            SingleTon.setUserObj(null);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkWritePermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_WRITE_EXTERNAL_STORAGE);
            return;
        } else {
            checkReadPermission();
        }
    }

    //TODO CHECK 3
    @TargetApi(Build.VERSION_CODES.M)
    private void checkReadPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_READ_EXTERNAL_STORAGE);
            return;
        } else {
            initActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_WRITE_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    checkReadPermission();
                } else {
                    checkWritePermission();
                }
                break;
            case MY_READ_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initActivity();
                } else {
                    checkReadPermission();
                }
                break;
        }
    }

    private void initActivity(){
        new Handler().postDelayed(new Runnable(){
            public void run(){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            };
        }, DURACION_SPLASH);
    }

}
