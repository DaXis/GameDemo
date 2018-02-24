package com.gamedemo.objs;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class HomeObj {

    public String titulo, resumen, urlNote, urlImg, color, backImg, urlYtb, date, rootPage;
    public Drawable bitmap;
    public long dateLong;
    public int id;

    private String genVideoID(String url){
        if(url.length() > 0){
            Log.d(titulo, url);
            url = url.replace("//","");
            String[] aux = url.split("/");
            String[] aux0 = aux[2].split("[?]");
            String key = aux0[0].replace("null","");
            Log.v("youtube video key", ""+key);
            return key;
        } else
            return "";
    }

    public String getYtbId(){
        return genVideoID(urlYtb);
    }

}
