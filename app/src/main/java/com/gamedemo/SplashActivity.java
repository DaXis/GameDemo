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

    private final int DURACION_SPLASH = 3000; // 3 segundos

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        checkLogin();

        new Handler().postDelayed(new Runnable(){
            public void run(){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            };
        }, DURACION_SPLASH);
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
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                JSONObject user = jsonArray.getJSONObject(0);
                UserObj userObj = new UserObj();
                userObj.id = user.getInt("id");
                userObj.username = user.getString("username");
                userObj.nombre = user.getString("nombre");
                userObj.apellidos = user.getString("apellidos");
                userObj.email = user.getString("email");
                userObj.telefono = user.getString("telefono");
                userObj.googleId = user.getString("googleId");
                userObj.googleOtr = user.getString("googleOtr");
                userObj.image = user.getString("image");

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

}
