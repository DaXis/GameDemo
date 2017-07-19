package com.gamedemo.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.gamedemo.SingleTon;
import com.gamedemo.fragments.HomeFragment;
import com.gamedemo.fragments.LoginFragment;
import com.gamedemo.fragments.NoteFragment;
import com.gamedemo.fragments.RegisterFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ConnectToServer {

    private ParseHome htmlToJson;
    private ParseNote parseNote;

    /**
     * @param args
     * index 0 String[] urls
     * index 1 int indicator
     * index 2 root class object
     */
    public ConnectToServer(Object[] args){
        new InitParse().executeOnExecutor(SingleTon.getsExecutor(), args);
    }

    public class InitParse extends AsyncTask<Object[], String, String> {

        private Object[] aux;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.gc();
        }

        @Override
        protected String doInBackground(Object[]... params) {
            aux = params[0];
            String[] urls = (String[])aux[0];
            String url;

            Object object = aux[2];
            if(object.getClass() == HomeFragment.class){
                htmlToJson = new ParseHome();

                for(int i = 0; i < urls.length; i++){
                    url = urls[i];
                    htmlToJson.initParse(url);
                }
            } else if(object.getClass() == NoteFragment.class){
                parseNote = new ParseNote(urls[0]);
                return parseNote.getBody();
            }

            return "";
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
        }

        protected void onPostExecute (String result){
            Log.d("onPostExecute", result.toString());
            decideMethod((int)aux[1], aux[2], result);
        }
    }

    private void decideMethod(int i, Object o, String result){
        if(i == 0){
            HomeFragment homeFragment = (HomeFragment)o;
            homeFragment.getResult();
        }
        if(i == 1){
            NoteFragment noteFragment = (NoteFragment)o;
            noteFragment.getResult(result);
        }
        if(i == 2){
            RegisterFragment registerFragment = (RegisterFragment)o;
            registerFragment.getResult(result);
        }
        if(i == 3){
            LoginFragment loginFragment = (LoginFragment)o;
            loginFragment.getResult(result);
        }
    }

    public ConnectToServer(String url,ArrayList<NameValuePair> nameValuePairs, int index, Object object){
        Object[] args = {url, nameValuePairs, index, object};
        new InitConnection().executeOnExecutor(SingleTon.getsExecutor(), args);
    }

    public class InitConnection extends AsyncTask<Object[], String, String> {

        private Object[] aux;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.gc();
        }

        @Override
        protected String doInBackground(Object[]... params) {
            aux = params[0];
            String url = (String)aux[0];
            ArrayList<NameValuePair> nameValuePairs = (ArrayList<NameValuePair>)aux[1];
            Log.d("user", nameValuePairs.get(1).getValue());
            InputStream is = null;
            String res = "Error";

            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                res = convertInputStreamToString(is);
                return res;
            }catch(Exception e){
                return res;
            }
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
        }

        protected void onPostExecute (String result){
            Log.d("onPostExecute", result);
            decideMethod((int)aux[2], aux[3], result);
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        bufferedReader.close();

        return result;
    }

}
