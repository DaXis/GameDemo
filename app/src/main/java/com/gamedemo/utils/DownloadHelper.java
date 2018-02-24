package com.gamedemo.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gamedemo.SingleTon;
import com.gamedemo.dialogs.DownloadDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DownloadHelper implements Serializable{

    private Activity activity;
    private DownloadDialog downloadDialog;

    public DownloadHelper(Activity activity){
        this.activity = activity;
    }

    /**
     * Metodo para descargar un archivo
     * @param params arreglo de objetos: (1)String url (2)String uri del directorio para el archivo (3)TextView para descripcion de esta
     *               (4)ProgressBar para imprimir el progreso
     * @param downloadDialog Objeto del dialogo para cerrarlo terminando la tarea
     * @return estado de la conexion
     */
    public String downloadFile(Object[] params, DownloadDialog downloadDialog){
        this.downloadDialog = downloadDialog;
        if(SingleTon.isOnline()){
            DownloadFileAsync fileD = new DownloadFileAsync();
            try {
                fileD.executeOnExecutor(SingleTon.getsExecutor(), params);
                return fileD.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "error";
            } catch (ExecutionException e) {
                e.printStackTrace();
                return "error";
            }
        } else
            return "Problema de conexion";
    }

    private class DownloadFileAsync extends AsyncTask<Object[], String, String> {
        int one = 0;
        private ProgressBar progressBar;
        private TextView textView;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Object[]... params) {
            int count;
            Object[] aux = params[0];

            textView = (TextView)aux[2];
            progressBar = (ProgressBar)aux[3];

            try {
                URL url = new URL((String)aux[0]);
                String nFile = getFileName((String)aux[0]);
                String uri = (String)aux[1]+"/"+renameFile(nFile);
                URLConnection conexion = url.openConnection();
                conexion.setRequestProperty("Accept-Encoding", "identity");
                conexion.connect();

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(uri);

                byte data[] = new byte[1024];
                long total = 0;
                int lenghtOfFile = conexion.getContentLength();
                //Log.i("final file path", "" + uri);
                Log.d("A Lenght of file ", "" + lenghtOfFile);

                while ((count = input.read(data)) != -1) {
                    total += count;
                    int porcent = (int)((total*100)/lenghtOfFile);
                    publishProgress(""+porcent);
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                return "Descarga finalizada";

            } catch (Exception e) {
                e.printStackTrace();
                return "Error al descargar";
            }
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            progressBar.setProgress(Integer.parseInt(progress[0]));
            textView.setText("descargado el "+progress[0]+"%");
            one++;
        }

        @Override
        protected void onPostExecute(String result) {
            if(one <= 1)
                result = "Error al descargar";
            super.onPostExecute(result);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    downloadDialog.dismiss();
                }
            }, 3000);
        }
    }

    /*private boolean fileExist(String uri){
        File file = new File(uri);
        if(file.isFile()){
            if(file.exists())
                return true;
            else
                return false;
        } else
            return false;
    }*/

    private String getFileName(String url){
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        return fileName;
    }

    private String renameFile(String nFile){
        ArrayList<String> files = new ArrayList<String>();

        files.clear();
        File carpet = SingleTon.getDownCarpet();
        for(File f : carpet.listFiles()) {
            if(f.isFile()){
                if(f.getName().contains(nFile)){
                    files.add(f.getName());
                }
            }
        }

        if(files.size() > 0) {
            String[] aux = nFile.split(".");
            aux[0] = aux[0].replace("null", "");
            aux[1] = aux[1].replace("null", "");
            nFile = aux[0]+files.size()+"."+aux[1];
        }
        return nFile;
    }

}
