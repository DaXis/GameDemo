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
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.TextView;

import com.gamedemo.db.DBHelper;
import com.gamedemo.dialogs.LoadDialog;
import com.gamedemo.objs.UserObj;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;

public class SingleTon extends Application {

    private static SingleTon m_Instance;

    private static Context context;
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
    private static CircleImageView profile;
    private static TextView username, email;
    private static MenuItem login;

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
        initPreferences();
    }

    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    private static File cache;
    private void genCacheDataCarpet(){
        if(cache == null)
            cache = new File(Environment.getExternalStorageDirectory(), "Games");
        if (!cache.exists()) {
            cache.mkdirs();
        }
    }

    public static File getCacheCarpet(){
        return cache;
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

    public static void showLoadDialog(FragmentManager manager){
        load = LoadDialog.newInstance();
        load.setCancelable(false);
        load.show(manager, "load dialog");
    }

    public static void dissmissLoad(){
        if(load != null)
            load.dismiss();
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

    public static void setProfile(CircleImageView arg){
        profile = arg;
    }

    public static CircleImageView getProfile(){
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

}
