package com.gamedemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gamedemo.SingleTon;

/**
 * Created by daxis on 13/06/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private Context ctx;

    final String sqlCreate0 = "CREATE TABLE Home (id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, resumen TEXT, urlNote TEXT," +
            "urlImg TEXT, color TEXT, backImg TEXT, urlYtb TEXT, date TEXT, rootPage TEXT, dateLong LONG)";

    final String[] create = {sqlCreate0};

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(int i = 0; i < create.length; i++){
            db.execSQL(create[i]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertToHome(String titulo, String resumen, String urlNote, String urlImg, String color, String backImg, String urlYtb,
                             String date, String rootPage, long dateLong){
        SingleTon.getDb().execSQL("INSERT INTO Home(titulo,resumen,urlNote,urlImg,color,backImg,urlYtb,date,rootPage,dateLong) " +
                "VALUES('"+titulo+"','"+resumen+"','"+urlNote+"','"+urlImg+"','"+color+"','"+backImg+"','"+urlYtb+"','"+date+"','"+
                rootPage+"',"+dateLong+")");
    }

}
