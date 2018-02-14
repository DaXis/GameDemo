package com.gamedemo.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.gamedemo.R;
import com.gamedemo.SingleTon;
import com.gamedemo.custom.ExpandableHeightGridView;
import com.gamedemo.utils.BlurEffect;
import com.gamedemo.utils.ConnectToServer;
import com.gamedemo.adapters.GalleryAdapter;
import com.gamedemo.utils.ParseNote;
import com.gamedemo.adapters.YoutubeFragment;
import com.gamedemo.utils.TextFileMannager;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoteFragment extends Fragment implements View.OnClickListener, YouTubePlayer.OnInitializedListener{

    private View rootView;
    private Bundle bundle;
    private String url, title, root;
    private LinearLayout scrollView;
    private ArrayList<String> nota = new ArrayList<String>(), urls = new ArrayList<String>();
    private String videoID = "";
    private RelativeLayout fullImg;
    private ImageView menuImg, close_button, donwload;
    private ViewPager viewPager;
    private ImageFragmentAdapter imageAdapter;
    //private CirclePageIndicator circlePageIndicator;
    private int imgId, id;
    private PopupMenu popup;
    private YouTubePlayer youTubePlayer;
    //private static TwitterActivities twitter;
    //private static FacebookActivities facebook;
    //private WebView content;
    //private static DownloadHelper downloadHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        id = bundle.getInt("id");
        url = bundle.getString("url");
        title = bundle.getString("title");
        root = bundle.getString("root");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume(){
        super.onResume();
        SingleTon.setCurrentFragment(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(youTubePlayer != null){
            if(youTubePlayer.isPlaying())
                youTubePlayer.pause();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(youTubePlayer != null){
            if(youTubePlayer.isPlaying())
                youTubePlayer.release();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_frag, container, false);
        this.rootView = rootView;

        Log.d("note url", url);
        SingleTon.showLoadDialog(this.getFragmentManager());
        if(!SingleTon.getBdh().getNota(id)){
            if(SingleTon.isOnline()){
                String[] urls = {url};
                Object[] args = {urls,1,this,id};
                ConnectToServer connectToServer = new ConnectToServer(args, root);
            }
        } else {
            if(new File(SingleTon.getCacheCarpet(), ""+id).exists()){
                Log.d("offline exist "+id, ""+true);
                ParseNote parseNote = new ParseNote(url, false, id);
                getResult(parseNote);
            } else {
                if(SingleTon.isOnline()){
                    String[] urls = {url};
                    Object[] args = {urls,1,this,id};
                    ConnectToServer connectToServer = new ConnectToServer(args, root);
                }
            }
        }

        return rootView;
    }

    public void getResult(ParseNote res) {
        if(!SingleTon.getBdh().getNota(id)) {
            SingleTon.getBdh().insertToNota(id, url, title, root, new File(SingleTon.getCacheCarpet(), ""+id).getAbsolutePath());
        }
        initViews(res);
        SingleTon.dissmissLoad();
    }

    private void initViews(ParseNote res){
        scrollView = (LinearLayout)rootView.findViewById(R.id.scrollView);
        fullImg = (RelativeLayout)rootView.findViewById(R.id.fullImg);
        close_button = (ImageView)rootView.findViewById(R.id.close_button);
        close_button.setOnClickListener(this);
        donwload = (ImageView)rootView.findViewById(R.id.download);
        donwload.setOnClickListener(this);
        viewPager = (ViewPager)rootView.findViewById(R.id.imagePager);
        //img_full = (ImageView)findViewById(R.id.img_full);
        //seec = (ProgressBar)findViewById(R.id.seec);

        TextView titleTv = (TextView)rootView.findViewById(R.id.title);
        titleTv.setText(Html.fromHtml(title));

        //final LinearLayout ll = (LinearLayout)findViewById(R.id.titleLL);
        final CardView ll = (CardView)rootView.findViewById(R.id.titleLL);

        /*Log.i("color", ""+color);
        if(!color.equals("NO"))
            ll.setBackgroundColor(Color.parseColor(color));

        if(!picBack.equals("NO"))
            ll.setBackgroundColor(Color.GRAY);

        String note = getSaveNote(titulo);*/

        /*if(note != null){
            if(progress.getVisibility() == View.VISIBLE)
                progress.setVisibility(View.GONE);

            if(menuImg.getVisibility() == View.GONE)
                menuImg.setVisibility(View.VISIBLE);

            reparseNote(note);
            putViews();
            parseToGetGallery(note);
            initImageAdapter();
        } else
            new loadNote().executeOnExecutor(Memory.getThreadPoolExecutor());*/

        //Log.v("notas --->", res.body);
        if(root.contains("LevelUp")){
            reparseLvUpNote(res.body);
            setLvUpImg(res.imgLvl);
            putViews();
        } else {
            reparseNote(res.body);
            putViews();
            parseToGetGallery(res.body);
            initImageAdapter();
        }
    }

    //TODO OnClick
    @Override
    public void onClick(View v) {
        switch(v.getId()){

        }
    }

    /*private void downloadDialog(String url, String directory){
        DownloadDialog downloadDialog = DownloadDialog.newInstance(downloadHelper,url, directory);
        downloadDialog.setCancelable(false);
        downloadDialog.show(getSupportFragmentManager(), "download dialog");
    }*/

    /*public class loadNote extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            nh = new NoteHelper(url);
            if(!nh.initParse())
                initInternetFaileDialog();

            return null;
        }

        protected void onPostExecute (Void result){
            if(!NotaDemo.this.isDestroyed()){
                if(nh.getNote().size() > 2){
                    //generateNoteOnSD(titulo, nh.getNote().get(2).getNote());
                    Memory.getBdh().insertIntoNote(titulo, nh.getNote().get(2).getNote(),
                            nh.getNote().get(2).getHtml());

                    reparseNote(nh.getNote().get(2).getNote());
                    putViews();
                    parseToGetGallery(nh.getNote().get(2).getNote());
                    initImageAdapter();

                    if(progress.getVisibility() == View.VISIBLE)
                        progress.setVisibility(View.GONE);

                    if(menuImg.getVisibility() == View.GONE)
                        menuImg.setVisibility(View.VISIBLE);
                }
            }
        }
    }*/

    private String getSaveNote(String title){
        String note = null;

        String[] args = {title};
        Cursor c = SingleTon.getDb().rawQuery("SELECT nota"
                + " FROM Notes WHERE titulo LIKE ?", args);
        if(c.moveToFirst()){
            do{
                note = c.getString(0);
            } while(c.moveToNext());
        }
        c.close();

        return note;
    }

    private boolean isFavoriteNote(String title){
        boolean favorite = false;
        int fav = 0;
        String[] args = {title};
        Cursor c = SingleTon.getDb().rawQuery("SELECT favorite"
                + " FROM Notes WHERE titulo LIKE ?", args);
        if(c.moveToFirst()){
            do{
                fav = c.getInt(0);
            } while(c.moveToNext());
        }
        c.close();

        if(fav == 1)
            favorite = true;

        return favorite;
    }

    public void generateNoteOnSD(String sFileName, String sBody){
        try{
            File root = SingleTon.getCacheCarpet();
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, sFileName);
            FileWriter writer = new FileWriter(file);
            writer.append(sBody);
            writer.flush();
            writer.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void reparseNote(String html){
        final Document doc;
        nota.clear();

        doc = Jsoup.parse(html);
        final Elements e0 = doc.select("div.twelve");
        final Elements e1 = e0.select("p");
        if(!e0.isEmpty()){
            nota.add(e1.get(0).text());
            for(int i = 0; i < e1.size(); i++){
                nota.add(e1.get(i).html());
                Log.i(""+i,""+e1.get(i).html());
            }
        }
        Log.v("notes size ===>", "" + nota.size());
    }

    public void reparseLvUpNote(String html){
        nota.clear();

        final Document doc = Jsoup.parse(html);
        final Elements e0 = doc.select("p,iframe");
        if(!e0.isEmpty()){
            for(int i = 0; i < e0.size(); i++){
                if(e0.get(i).html().length() > 0)
                    nota.add(e0.get(i).html());
                else
                    nota.add(e0.get(i).toString());
                Log.d("nota.get("+i+")", nota.get(i));
            }
        }
        Log.v("notes size ===>", "" + nota.size());
    }

    private void setLvUpImg(String imgLvl){
        Log.d("imgLvl --->", imgLvl);
        ProgressBar spinner = new ProgressBar(getActivity());
        final ImageView img = new ImageView(getActivity());
        //img.setId(0);
        Picasso.with(getActivity()).load(imgLvl).into(img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        img.setPadding(15, 10, 15, 10);
        scrollView.addView(img);
    }

    private void putViews(){
        imgId = 0;
        for(int i = 0; i < nota.size(); i++){
            //Log.d("nota.get("+i+")", nota.get(i));
            if(nota.get(i).contains("href")) {
                if (nota.get(i).contains("youtube")){
                    parseToGetYoutubeVideo(nota.get(i), i, false);
                    continue;
                } else if(nota.get(i).contains("facebook")) {
                    parseToGetFBPost(nota.get(i));
                    continue;
                } else if(parseToGetImg(nota.get(i)))
                    continue;
                else {
                    parseToGetText(nota.get(i));
                    continue;
                }
            } else {
                if(nota.get(i).contains("iframe")){
                    parseToGetYoutubeVideo(nota.get(i), i, true);
                    continue;
                } else {
                    if(nota.get(i).contains("<img"))
                        parseToGetTextAndImgs(nota.get(i));
                    else
                        parseToGetText(nota.get(i));
                    continue;
                }
            }
        }
    }

    private void initImageAdapter(){
        imageAdapter = new ImageFragmentAdapter(getChildFragmentManager(), urls);
        viewPager.setAdapter(imageAdapter);
        /*circlePageIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        circlePageIndicator.setViewPager(viewPager);*/
    }

    private boolean parseToGetImg(String html){
        final Document doc;
        doc = Jsoup.parse(html);
        Elements e0 = doc.select("a");
        String url = e0.get(0).attr("href").toString();

        if(url.length() > 0) {
            if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".gif")) {
                FrameLayout frameLayout = new FrameLayout(getActivity());
                ProgressBar spinner = new ProgressBar(getActivity());

                final ImageView img = new ImageView(getActivity());
                img.setId(imgId);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("imgId", "" + img.getId());
                        if (fullImg.getVisibility() == View.GONE) {
                            fullImg.setBackground(getBluredView());
                            fullImg.setVisibility(View.VISIBLE);
                            viewPager.setCurrentItem(img.getId());
                        }
                    }
                });


                //img.setPadding(1, 1, 1, 1);
                //img.setScaleType(ImageView.ScaleType.FIT_XY);
                img.setPadding(15, 10, 15, 10);

                frameLayout.addView(img);
                frameLayout.addView(spinner);
                if(!url.contains("barrita")) {
                    //Memory.loadImage(url, img, spinner);
                    Picasso.with(getActivity()).load(url).into(img);
                    urls.add(url);
                    scrollView.addView(frameLayout);
                }
                imgId++;

                return true;
            } else {
                url = reparseForImg(html);
                if (url.length() > 0) {
                    if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".gif")) {
                        FrameLayout frameLayout = new FrameLayout(getActivity());
                        ProgressBar spinner = new ProgressBar(getActivity());

                        final ImageView img = new ImageView(getActivity());
                        img.setId(imgId);
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("imgId", "" + img.getId());
                                if (fullImg.getVisibility() == View.GONE) {
                                    fullImg.setVisibility(View.VISIBLE);
                                    viewPager.setCurrentItem(img.getId());
                                }
                            }
                        });

                        //img.setPadding(1, 1, 1, 1);
                        //img.setScaleType(ImageView.ScaleType.FIT_XY);
                        img.setPadding(15, 10, 15, 10);

                        frameLayout.addView(img);
                        frameLayout.addView(spinner);
                        if(!url.contains("barrita")) {
                            //Memory.loadImage(url, img, spinner);
                            Picasso.with(getActivity()).load(url).into(img);
                            urls.add(url);
                            scrollView.addView(frameLayout);
                        }

                        imgId++;

                        return true;
                    } else
                        return false;
                } else
                    return false;

            }
        } else
            return false;
    }

    private String reparseForImg(String html){
        final Document doc;
        doc = Jsoup.parse(html);
        Elements e0 = doc.select("a noscript img");

        if(e0.size() > 0){
            for(int i = 0; i < e0.size(); i++){
                String url = e0.get(i).attr("src").toString();
                Log.v("url --------", ""+url);
                if(url.length() > 0){
                    if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".gif"))
                        return url;
                    else
                        return "";
                } else
                    return "";
            }
            return "";
        } else
            return "";
    }

    private void parseToGetYoutubeVideo(String html, int id, boolean iframe){
        Log.d("youtube html", html);
        final Document doc;
        doc = Jsoup.parse(html);

        if(!iframe){
            parseToGetText(html);
        } else {
            parseToGetText(html);
            Elements e0 = doc.select("iframe");
            final String url = e0.get(0).attr("src").toString();

            Log.i("ytb url", "" + url);
            if(url.length() > 0){
                /*String[] aux = url.split("/");

                if(aux.length > 4) {
                    videoID = aux[4].replace("null", "");

                    if(videoID.length() <= 5){
                        String[] aux1 = aux[5].split("[?]");
                        videoID = aux1[0].replace("null", "");
                    }

                    videoID = videoID.replace("?rel=0", "");
                    videoID = videoID.replace("?feature=oembed", "");

                } else {
                    videoID = aux[3].replace("null", "");
                    videoID = videoID.replace("video_embed?id=", "");
                }*/

                //ejemplo de url ytb http://bleacherreport.com/video_embed?id=pobmdtdDrjhAbT4D3vDjqvLLw7YwwoCE

                videoID = getVideoID(url);
                Log.v("video id", "" + videoID);

                if(!videoID.equals("other")){
                    FrameLayout youLay = new FrameLayout(getActivity());
                    youLay.setId(id);
                    youLay.setPadding(15, 15, 15, 15);
                    scrollView.addView(youLay);
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    final YoutubeFragment youtube = new YoutubeFragment();
                    ft.add(youLay.getId(), youtube);
                    ft.commit();
                    youtube.initialize(getActivity().getResources().getString(R.string.API_KEY), this);

                } else {
                    LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View player = inflater.inflate(R.layout.video, null);

                    final VideoView videoView = (VideoView)player.findViewById(R.id.videoView);
                    final Button play = (Button)player.findViewById(R.id.play_btn);
                    play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            playVideo(videoView, play);
                        }
                    });

                    scrollView.addView(player);
                }
            }

        }
    }

    private void playVideo(final VideoView videoView, final Button play){
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setTitle("Cargando video");
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        try {
            MediaController mediacontroller = new MediaController(getActivity());
            mediacontroller.setAnchorView(videoView);

            Uri video = Uri.parse(url);
            videoView.setMediaController(mediacontroller);
            videoView.setVideoURI(video);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                play.setVisibility(View.GONE);
                pDialog.dismiss();
                videoView.start();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                pDialog.dismiss();
                return false;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
    }

    private String getVideoID(String url){
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if(matcher.find())
            return matcher.group();
        else
            return "other";
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        this.youTubePlayer = youTubePlayer;
        if(!b)
            youTubePlayer.loadVideo(videoID);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    private void parseToGetFBPost(String html){
        if(html.contains("href")){
            TextView txt = new TextView(getActivity());
            txt.setText(Html.fromHtml(html));
            txt.setPadding(15, 10, 15, 10);
            scrollView.addView(txt);
        } else {
            TextView txt = new TextView(getActivity());
            txt.setText("Post de Facebook");
            txt.setPadding(15,10,15,10);
            scrollView.addView(txt);
        }
    }

    private void parseToGetText(String nota){
        if(!nota.contains("<script>")){
            if(nota.contains("<span class")){
                String note = reparseToText(nota);
                if(note != null){
                    TextView txt = new TextView(getActivity());
                    txt.setText(Html.fromHtml(note));
                    txt.setPadding(15,10,15,10);
                    scrollView.addView(txt);
                }
            } else {
                TextView txt = new TextView(getActivity());
                txt.setText(Html.fromHtml(nota));
                txt.setPadding(15,10,15,10);
                scrollView.addView(txt);
            }
        }
    }

    private void parseToGetTextAndImgs(String nota){
        boolean addPic = false;
        final Document doc;
        doc = Jsoup.parse(nota);
        Elements e0 = doc.select("img");
        String url = e0.get(0).attr("src").toString();
        FrameLayout frameLayout = new FrameLayout(getActivity());
        ProgressBar spinner = new ProgressBar(getActivity());
        final ImageView img = new ImageView(getActivity());
        img.setId(imgId);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("imgId", "" + img.getId());
                if (fullImg.getVisibility() == View.GONE) {
                    fullImg.setBackground(getBluredView());
                    fullImg.setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem(img.getId());
                }
            }
        });
        img.setPadding(15, 10, 15, 10);
        frameLayout.addView(img);
        frameLayout.addView(spinner);
        if(!url.contains("barrita") && !url.contains("389289_832x14")) {
            Picasso.with(getActivity()).load(url).into(img);
            urls.add(url);
            scrollView.addView(frameLayout);
            addPic = true;
        }
        imgId++;
        spinner.setVisibility(View.GONE);

        if(!nota.contains("<script>")){
            if(nota.contains("<span class")){
                String note = reparseToText(nota);
                if(note != null){
                    TextView txt = new TextView(getActivity());
                    txt.setText(Html.fromHtml(note));
                    txt.setPadding(15,10,15,10);
                    scrollView.addView(txt);
                }
            } else {
                /*Elements e1 = doc.select("img.size-full");
                doc.*/
                if(!addPic){
                    TextView txt = new TextView(getActivity());
                    txt.setText(Html.fromHtml(nota));
                    txt.setPadding(15,10,15,10);
                    scrollView.addView(txt);
                }
            }
        }
    }

    private String reparseToText(String html){
        //Log.v("html to reparse", ""+html);
        final Document doc;
        doc = Jsoup.parse(html);
        Elements e0 = doc.select("span");
        String nota = e0.get(0).html();

        return nota;
    }

    private void parseToGetGallery(String html){
        final Document doc;
        doc = Jsoup.parse(html);
        Elements e0 = doc.select("div.gallery");
        if(e0.size() > 0)
            getGalleryImgs(e0.get(0).html());
    }

    private void getGalleryImgs(String html){
        final Document doc;
        doc = Jsoup.parse(html);
        Elements e0 = doc.select("a");
        final ArrayList<String> imgs = new ArrayList<String>();

        for(int i = 0; i < e0.size(); i++){
            String url = e0.get(i).attr("href").toString();
            imgs.add(url);
            urls.add(url);
        }

        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.grid_imgs, null);
        GalleryAdapter galleryAdapter = new GalleryAdapter(getActivity(), imgs);

        ExpandableHeightGridView gridView = (ExpandableHeightGridView)grid.findViewById(R.id.gridView);
        gridView.setExpanded(true);
        gridView.setAdapter(galleryAdapter);
        galleryAdapter.notifyDataSetChanged();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < imgs.size(); i++) {
                    if (i == position) {
                        displayImgFromGrid(imgs.get(i));
                        break;
                    }
                }
            }
        });

        scrollView.addView(grid);
    }

    private void displayImgFromGrid(String url){
        if(urls.contains(url)){
            if (fullImg.getVisibility() == View.GONE) {
                fullImg.setBackground(getBluredView());
                fullImg.setVisibility(View.VISIBLE);
                int id = urls.indexOf(url)+imgId;
                id = id-1;
                viewPager.setCurrentItem(id);
            }
        }
    }

    private void initInternetFaileDialog(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Hay un problema con tu conexion a internet");
                builder.setTitle("UPS!!!");
                builder.setCancelable(false);

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        onBackPressed();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void sharedIntent(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, url+"\nEnviado desde Atomix App");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private BitmapDrawable getBluredView(){
        View view = getActivity().getWindow().getDecorView().getRootView();
        //View view = getWindow().getDecorView().findViewById(android.R.id.content);
        Bitmap bitmap = null;
        BitmapDrawable drawableBitmap = null;
        if(view != null){
            bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);
            view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            view.draw(canvas);

            BlurEffect blurEffect = new BlurEffect(getActivity());
            drawableBitmap = new BitmapDrawable(null, blurEffect.blurBitmap(bitmap));
        }

        //getSupportActionBar().hide();
        return  drawableBitmap;
    }

    private void getScreenshot(){
        getActivity().getWindow().getDecorView().setDrawingCacheEnabled(true);
        BitmapDrawable drawableBitmap = new BitmapDrawable(null, getActivity().getWindow().getDecorView().getDrawingCache());
    }

    //@Override
    public void onBackPressed(){
        if (fullImg.getVisibility() == View.VISIBLE) {
            fullImg.setVisibility(View.GONE);
        } else {
            System.gc();
            getActivity().finish();
        }
    }

}

