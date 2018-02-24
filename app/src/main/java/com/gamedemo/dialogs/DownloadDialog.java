package com.gamedemo.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gamedemo.R;
import com.gamedemo.utils.DownloadHelper;

public class DownloadDialog extends DialogFragment {

    private String url, directory;
    private DownloadHelper downloadHelper;

    public static DownloadDialog newInstance(DownloadHelper downloadHelper, String url, String directory){
        DownloadDialog downloadDialog = new DownloadDialog();
        downloadDialog.downloadHelper = downloadHelper;

        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("directory", directory);
        args.putSerializable("downloadHelper", downloadHelper);
        downloadDialog.setArguments(args);

        return downloadDialog;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        url = getArguments().getString("url");
        directory = getArguments().getString("directory");
        downloadHelper = (DownloadHelper)getArguments().getSerializable("downloadHelper");
        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo;
        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.download, null);
        builder.setView(v);

        TextView title = (TextView)v.findViewById(R.id.donwloadTitle);
        title.setText(title.getText().toString()+"\""+getFileName(url)+"\" en el directorio "+directory);

        final TextView subTitle = (TextView)v.findViewById(R.id.download_subText);
        final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressDownload);
        progressBar.setMax(100);

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Object[] params = {url, directory, subTitle, progressBar};
                    downloadHelper.downloadFile(params, DownloadDialog.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


        return builder.create();
    }

    private String getFileName(String url){
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        return fileName;
    }

}
