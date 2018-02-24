package com.gamedemo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamedemo.db.DBHelper;
import com.gamedemo.dialogs.LoadDialog;
import com.gamedemo.objs.ScheduleObj;
import com.gamedemo.objs.UserObj;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleTon extends Application {

    private static SingleTon m_Instance;
    private static Context context;
    private static AppCompatActivity main;
    //----------------------
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 128;
    private static final int KEEP_ALIVE = 10;

    private static final BlockingQueue<Runnable> sWorkQueue =
            new LinkedBlockingQueue<Runnable>(KEEP_ALIVE);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "TrackingUserTask #" + mCount.getAndIncrement());
        }
    };

    private static final ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sWorkQueue, sThreadFactory);
    //----------------------
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    private static SQLiteDatabase db;
    private static DBHelper dbh;
    private static Fragment fragment;
    private static LoadDialog load;
    private static Toolbar toolbar;
    private static UserObj userObj;
    private static ImageView profile;
    private static TextView username, email;
    private static MenuItem login;
    private static File cache, down;
    private static ArrayList<ScheduleObj> array;

    public SingleTon() {
        super();
        m_Instance = this;
    }

    public static SingleTon getInstance() {
        if(m_Instance == null) {
            synchronized(SingleTon.class) {
                if(m_Instance == null) new SingleTon();
            }
        }
        return m_Instance;
    }

    public void onCreate() {
        super.onCreate();
        context = this;
        initBD();
        genCacheDataCarpet();
        genDownCarpet();
        initPreferences();
        initScheduleConfig();
    }

    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    private void genCacheDataCarpet(){
        if(cache == null)
            cache = context.getFilesDir();
        if (!cache.exists()) {
            cache.mkdirs();
        }
    }

    public static File getCacheCarpet(){
        return cache;
    }

    private void genDownCarpet(){
        if(down == null)
            down = new File(Environment.getExternalStorageDirectory(), "DownGames");
        if (!down.exists()) {
            down.mkdirs();
        }
    }

    public static File getDownCarpet(){
        return down;
    }

    private void initPreferences(){
        if(settings == null)
            settings = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        if(editor == null)
            editor = settings.edit();
    }

    public static SharedPreferences getSettings(){
        return settings;
    }

    public static SharedPreferences.Editor getEditor(){
        return editor;
    }

    private void initBD(){
        if(dbh == null)
            dbh = new DBHelper(this, "Games", null, 2);
        if(db == null)
            db = dbh.getWritableDatabase();
    }

    public static SQLiteDatabase getDb(){
        return db;
    }

    public static DBHelper getBdh(){
        return dbh;
    }

    public static int dpTpPx(Context context, int dp){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(Context context, int px){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static ThreadPoolExecutor getsExecutor(){
        return sExecutor;
    }

    public static LoadDialog showLoadDialog(FragmentManager manager){
        if(load == null){
            synchronized (SingleTon.class){
                if(load == null){
                    load = LoadDialog.newInstance();
                    load.setCancelable(false);
                    try{
                        load.show(manager, "load dialog");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return load;
    }

    public static void dissmissLoad(){
        try {
            if(load != null) {
                try{
                    load.dismiss();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                load = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCurrentFragment(Fragment arg){
        fragment = arg;
    }

    public static Fragment getCurrentFragment(){
        return fragment;
    }

    public static Drawable LoadImageFromURL(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setToolbar(Toolbar arg){
        toolbar = arg;
    }

    public static Toolbar getToolbar(){
        return toolbar;
    }

    public static void setUserObj(UserObj arg){
        userObj = arg;
    }

    public static UserObj getUserObj(){
        return userObj;
    }

    public static void setProfile(ImageView arg){
        profile = arg;
    }

    public static ImageView getProfile(){
        return profile;
    }

    public static void setEmail(TextView arg){
        email = arg;
    }

    public static TextView getEmail(){
        return email;
    }

    public static void setUsername(TextView arg){
        username = arg;
    }

    public static TextView getUsername(){
        return username;
    }

    public static void setLogin(MenuItem arg){
        login = arg;
    }

    public static MenuItem getLogin(){
        return login;
    }

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void setCurrenActivity(AppCompatActivity arg){
        main = arg;
    }

    public static AppCompatActivity getCurrentActivity(){
        return main;
    }

    public static void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null)
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void savePreferences(String tag, String arg){
        editor.putString(tag, arg);
        editor.apply();
    }

    public static void savePreferences(String tag, int arg){
        editor.putInt(tag, arg);
        editor.apply();
    }

    public static void savePreferences(String tag, boolean arg){
        editor.putBoolean(tag, arg);
        editor.apply();
    }

    public static void savePreferences(String tag, long arg){
        editor.putLong(tag, arg);
        editor.apply();
    }

    private static void initScheduleConfig(){
        array = new ArrayList<>();
        ScheduleObj scheduleObj0 = new ScheduleObj();
        scheduleObj0.hint = "5 minutos";
        scheduleObj0.value = TimeUnit.MINUTES.toMillis(5);

        ScheduleObj scheduleObj1 = new ScheduleObj();
        scheduleObj1.hint = "10 minutos";
        scheduleObj1.value = TimeUnit.MINUTES.toMillis(10);

        ScheduleObj scheduleObj2 = new ScheduleObj();
        scheduleObj2.hint = "15 minutos";
        scheduleObj2.value = TimeUnit.MINUTES.toMillis(15);

        ScheduleObj scheduleObj3 = new ScheduleObj();
        scheduleObj3.hint = "30 minutos";
        scheduleObj3.value = TimeUnit.MINUTES.toMillis(30);

        ScheduleObj scheduleObj4 = new ScheduleObj();
        scheduleObj4.hint = "1 hora";
        scheduleObj4.value = TimeUnit.HOURS.toMillis(1);

        ScheduleObj scheduleObj5 = new ScheduleObj();
        scheduleObj5.hint = "2 horas";
        scheduleObj5.value = TimeUnit.HOURS.toMillis(2);

        array.add(scheduleObj0);
        array.add(scheduleObj1);
        array.add(scheduleObj2);
        array.add(scheduleObj3);
        array.add(scheduleObj4);
        array.add(scheduleObj5);
    }

    public static ArrayList<ScheduleObj> getScheduleConfig(){
        return array;
    }

}
