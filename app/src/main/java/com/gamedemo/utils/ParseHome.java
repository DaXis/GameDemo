package com.gamedemo.utils;

import android.database.Cursor;
import android.util.Log;

import com.gamedemo.SingleTon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ParseHome {

    private String url;
    private String title = "", resum = "", urlNote = "", urlImg = "", urlYouTube = "", color = "", backImg = "", date = "";
    private long dateLong = 0;

    public ParseHome(){

    }

    public void initParse(String url){
        this.url = url;

        if(url.contains("atomix.vg"))
            initParseAtomix();
        else if(url.contains("levelup.com"))
            initLevelUpParse(url);
    }

    private boolean initParseAtomix(){
        final Document doc;
        try {
            doc = Jsoup.connect(url).get();
            final Elements e0 = doc.select("div.post");
            for(int i = 0; i < e0.size(); i++){
                parseBlock(e0.get(i).toString());
            }
            System.gc();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.gc();
            return false;
        }
    }

    private void parseBlock(String block){
        title = "";
        resum = "";
        urlNote = "";
        urlImg = "";
        urlYouTube = "";
        color = "";
        backImg = "";
        date = "";

        final Document doc = Jsoup.parse(block);

        final Elements e0 = doc.select("div.twelve h1 a");
        final Elements e1 = doc.select("div.twelve p");
        final Elements e2 = doc.select("div.wide-overlay");
        final Elements e3 = doc.select("style");
        final Elements e4 = doc.select("iframe");
        final Elements e5 = doc.select("div.wide-overlay img");
        final Elements e6 = doc.select("span.date");

        if(!e0.isEmpty()){
            title = e0.get(0).text();
            urlNote = e0.get(0).attr("href").toString();
            title = title.replace("'","''");
        }

        if(!e1.isEmpty()){
            for(int i = 0; i < e1.size(); i++){
                resum = e1.get(i).text();
            }
            resum = resum.replace("'","''");
        }

        if(!e2.isEmpty()){
            if(!e2.get(0).attr("style").toString().equals("")){
                color = e2.get(0).attr("style").toString();
                color = color.replace("background:","");
            }
        }

        if(!e3.isEmpty()){
            final String[] trash = e3.get(0).toString().split("[()]");
            if(!trash[1].equals("")){
                backImg = trash[1];
            }
        }

        //Log.d("e4 "+title, "---> "+e4.toString());
        if(!e4.isEmpty()){
            //Log.d("youtube ", e4.get(0).attr("src").toString());
            //urlYouTube = e4.get(0).attr("src").toString().replace("//", "");
            //Log.d("youtube ", e4.get(0).attr("src").toString());
            urlYouTube = e4.get(0).attr("src").toString();
        }

        if(!e5.isEmpty()){
            if(!e5.get(0).attr("src").toString().contains("facebook.png")){
                urlImg = e5.get(0).attr("src").toString();
            }
        }

        if(!e6.isEmpty()){
            date = e6.get(0).text();
            date = parseDate(date);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            try {
                Date dateF = format.parse(date);
                dateLong = dateF.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(!existentHomeNote(title))
            SingleTon.getBdh().insertToHome(title, resum, urlNote, urlImg, color, backImg, urlYouTube, date, "Atomix", dateLong);
    }

    public String parseDate(String time) {
        /*String inputPattern = "dd/MM/yyyy hh:mm a";
        String outputPattern = "dd/MM/yyyy HH:mm ";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;

        try {
            date = inputFormat.parse(time);
            time = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        time = dateFormat(getDateFromString(time).getTime());

        return time;
    }

    public static Date getDateFromString(String fecha){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ROOT);
        Date date = null;
        try {
            date = format.parse(fecha);
            //System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private static String dateFormat(long time){
        String date = "";
        date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(time));
        return date;
    }

    //TODO level up
    public boolean initLevelUpParse(String url){
        final Document doc;
        try {
            doc = Jsoup.connect(url).get();
            final Elements e0 = doc.select(".cf");
            for(int i = 0; i < e0.size(); i++){
                parseLevelUpBlock(e0.get(i).toString());
            }
            System.gc();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.gc();
            return false;
        }
    }

    private void parseLevelUpBlock(String block) {
        final Document doc = Jsoup.parse(block);

        final Elements e0 = doc.select("div.img");
        for(int i = 0; i < e0.size(); i++){
            String html = e0.get(i).toString();
            desglos(html);
        }
        final Elements e1 = doc.select("div.content");
        for(int i = 0; i < e1.size(); i++){
            String html = e1.get(i).toString();
            complement(html);
        }


        if(!existentHomeNote(title))
            SingleTon.getBdh().insertToHome(title, resum, urlNote, urlImg, color, backImg, urlYouTube, date, "LevelUp", dateLong);
    }

    private void desglos(String block){
        title = "";
        resum = "";
        urlNote = "";
        urlImg = "";
        urlYouTube = "";
        color = "";
        backImg = "";
        date = "";

        final Document doc = Jsoup.parse(block);

        final Elements e0 = doc.select("a");
        final Elements e1 = doc.select(".image");
        final Elements e2 = doc.select("a img");

        urlNote = e0.get(0).attr("href").toString();
        urlNote = "http://www.levelup.com/"+urlNote;
        title = e0.get(0).attr("title").toString();
        title = title.replace("'","''");

        if(e1.size() > 0)
            urlImg = getRealUrl(e1.get(0).attr("style").toString());
        else
            urlImg = e2.get(0).attr("src").toString();
    }

    private void complement(String block){
        final Document doc = Jsoup.parse(block);

        final Elements e3 = doc.select("p.elementIntro");

        if(e3.size() > 0)
            resum = e3.get(0).text();
        else
            resum = "";

        resum = resum.replace("'","''");

        final Elements e4 = doc.select("div p.time time");
        if(e4.toString().length() > 0){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = null;
            String time = e4.get(0).attr("datetime");
            time = time.replace("T", " ");
            time = time.replace("+00:00", "");
            try {
                date = format.parse(time);
                dateLong = date.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                dateFormat.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
                this.date = dateFormat.format(new Date(dateLong));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            long time = System.currentTimeMillis();
            date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(time));
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            try {
                Date dateF = format.parse(date);
                dateLong = dateF.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    private String getRealUrl(String clear){
        clear = clear.replace("background-image:url('","");
        clear = clear.replace("')","");
        return clear;
    }

    private boolean existentHomeNote(String title){
        boolean exist = false;

        String[] args = {title};
        Cursor c = SingleTon.getDb().rawQuery("SELECT *"
                + " FROM Home WHERE titulo LIKE ?", args);
        if(c.moveToFirst()){
            do{
                exist = true;
                break;
            } while(c.moveToNext());
        }
        c.close();

        return exist;
    }

}
