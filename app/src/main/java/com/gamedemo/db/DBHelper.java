package com.gamedemo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gamedemo.SingleTon;
import com.gamedemo.objs.HomeObj;

import java.util.ArrayList;

/**
 * Created by daxis on 13/06/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private Context ctx;

    final String sqlCreate0 = "CREATE TABLE Home (id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, resumen TEXT, urlNote TEXT," +
            "urlImg TEXT, color TEXT, backImg TEXT, urlYtb TEXT, date TEXT, rootPage TEXT, dateLong LONG)";

    final String sqlCreate1 = "CREATE TABLE Nota (id INTEGER, url TEXT, titulo TEXT, root TEXT, path TEXT)";

    final String[] create = {sqlCreate0, sqlCreate1};

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

    public ArrayList<HomeObj> getHomeNotes(){
        ArrayList<HomeObj> homeObjs = new ArrayList<>();
        Cursor c = SingleTon.getDb().rawQuery("SELECT titulo,resumen,urlNote,urlImg,color,backImg,urlYtb,date,rootPage,dateLong, id"
                + " FROM Home ORDER BY date DESC", null);
        if(c.moveToFirst()){
            do{
                HomeObj hn = new HomeObj();
                hn.titulo = c.getString(0);
                hn.resumen = c.getString(1);
                hn.urlNote = c.getString(2);
                hn.urlImg = c.getString(3);
                hn.color = c.getString(4);
                hn.backImg = c.getString(5);
                hn.urlYtb = c.getString(6);
                hn.date = c.getString(7);
                hn.rootPage = c.getString(8);
                hn.dateLong = c.getLong(9);
                hn.id = c.getInt(10);
                homeObjs.add(hn);
            } while(c.moveToNext());
        }
        c.close();
        return homeObjs;
    }

    public ArrayList<HomeObj> getAtomixNotes(){
        ArrayList<HomeObj> homeObjs = new ArrayList<>();
        Cursor c = SingleTon.getDb().rawQuery("SELECT titulo,resumen,urlNote,urlImg,color,backImg,urlYtb,date,rootPage,dateLong, id"
                + " FROM Home WHERE rootPage = 'Atomix' ORDER BY date DESC", null);
        if(c.moveToFirst()){
            do{
                HomeObj hn = new HomeObj();
                hn.titulo = c.getString(0);
                hn.resumen = c.getString(1);
                hn.urlNote = c.getString(2);
                hn.urlImg = c.getString(3);
                hn.color = c.getString(4);
                hn.backImg = c.getString(5);
                hn.urlYtb = c.getString(6);
                hn.date = c.getString(7);
                hn.rootPage = c.getString(8);
                hn.dateLong = c.getLong(9);
                hn.id = c.getInt(10);
                homeObjs.add(hn);
            } while(c.moveToNext());
        }
        c.close();
        return homeObjs;
    }

    public ArrayList<HomeObj> getLvUpNotes(){
        ArrayList<HomeObj> homeObjs = new ArrayList<>();
        Cursor c = SingleTon.getDb().rawQuery("SELECT titulo,resumen,urlNote,urlImg,color,backImg,urlYtb,date,rootPage,dateLong, id"
                + " FROM Home WHERE rootPage = 'LevelUp' ORDER BY date DESC", null);
        if(c.moveToFirst()){
            do{
                HomeObj hn = new HomeObj();
                hn.titulo = c.getString(0);
                hn.resumen = c.getString(1);
                hn.urlNote = c.getString(2);
                hn.urlImg = c.getString(3);
                hn.color = c.getString(4);
                hn.backImg = c.getString(5);
                hn.urlYtb = c.getString(6);
                hn.date = c.getString(7);
                hn.rootPage = c.getString(8);
                hn.dateLong = c.getLong(9);
                hn.id = c.getInt(10);
                homeObjs.add(hn);
            } while(c.moveToNext());
        }
        c.close();
        return homeObjs;
    }

    public void insertToNota(int id, String url, String titulo, String root, String path){
        SingleTon.getDb().execSQL("INSERT INTO Nota(id, url, titulo, root, path) " +
                "VALUES("+id+",'"+url+"','"+titulo+"','"+root+"','"+path+"')");
    }

    public boolean getNota(int id){
        boolean exist = false;
        String query = "SELECT id, url, titulo, root, path FROM Nota WHERE id ="+id;
        Cursor c = SingleTon.getDb().rawQuery(query, null);
        if(c.moveToFirst()){
            do{
                exist = true;
            } while(c.moveToNext());
        }
        c.close();
        return exist;
    }

}
