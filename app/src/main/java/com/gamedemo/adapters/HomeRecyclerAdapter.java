package com.gamedemo.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gamedemo.R;
import com.gamedemo.SingleTon;
import com.gamedemo.interfaces.OnItemClick;
import com.gamedemo.objs.HomeObj;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>
        implements YouTubePlayer.OnInitializedListener {

    private static ArrayList<HomeObj> homeNote = new ArrayList<HomeObj>();
    private Activity activity;
    private FragmentManager fragmentManager;
    private String videoID;
    private static OnItemClick onItemClick;
    private int[] colors;

    public HomeRecyclerAdapter(Activity activity, ArrayList<HomeObj> homeNote, FragmentManager fragmentManager) {
        this.activity = activity;
        this.homeNote = homeNote;
        colors = new int[]{activity.getResources().getColor(R.color.accent_color),
                            activity.getResources().getColor(R.color.primary_color),
                            activity.getResources().getColor(R.color.dark_primary_color),
                            activity.getResources().getColor(R.color.pager_back),
                            activity.getResources().getColor(R.color.orange),
                            activity.getResources().getColor(R.color.divider_color)};
        this.fragmentManager = fragmentManager;
    }

    @Override
    public HomeRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(homeNote.get(position) != null){

            holder.text.setText(Html.fromHtml(homeNote.get(position).titulo));
            holder.content.setText(homeNote.get(position).resumen);
            holder.fuente.setText(homeNote.get(position).rootPage);
            holder.time.setText(homeNote.get(position).date);

            if(homeNote.get(position).backImg.length() > 0){
                if(homeNote.get(position).bitmap == null)
                    setBitmap(homeNote.get(position).backImg, holder.ll, position);
                else
                    holder.ll.setBackground(homeNote.get(position).bitmap);
            } else {
                if(homeNote.get(position).color.length() > 1)
                    holder.ll.setBackgroundColor(Color.parseColor(colorIssue(homeNote.get(position).color)));
                else
                    holder.ll.setBackgroundColor(getRandomColor());
            }

            if(homeNote.get(position).urlImg.length() > 0){
                holder.image.setVisibility(View.VISIBLE);
                Picasso.with(activity).load(homeNote.get(position).urlImg).into(holder.image);
                //Picasso.with(activity).setIndicatorsEnabled(true);
            } else
                holder.image.setVisibility(View.GONE);

            if(homeNote.get(position).urlYtb.length() > 0){
                if(homeNote.get(position).urlYtb.contains("youtube")){
                    final FragmentTransaction ft = fragmentManager.beginTransaction();
                    final YoutubeFragment youtube = new YoutubeFragment();
                    holder.youLay.setVisibility(View.VISIBLE);
                    videoID = genVideoID(homeNote.get(position).urlYtb);
                    ft.add(R.id.youtube, youtube).commit();
                    youtube.initialize(activity.getResources().getString(R.string.API_KEY), this);
                }
            } else
                holder.youLay.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return homeNote.size();
    }

    public HomeObj getItem(int position){
        return homeNote.get(position);
    }

    private void setBitmap(String url, final LinearLayout ll, int position){
        homeNote.get(position).bitmap = SingleTon.LoadImageFromURL(url);
        ll.setBackground(homeNote.get(position).bitmap);
    }

    private String colorIssue(String color){
        if(!color.contains("#")){
            return "#"+color;
        } else
            return color;
    }

    private String genVideoID(String url){
        String[] aux = url.split("/");
        String[] aux0 = aux[2].split("[?]");
        String key = aux0[0].replace("null","");
        Log.v("youtube video key", ""+key);
        return key;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(!b)
            youTubePlayer.loadVideo(videoID);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    public void updateAdapter(ArrayList<HomeObj> homeNote){
        HomeRecyclerAdapter.homeNote = homeNote;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final LinearLayout ll;
        public final TextView text, content, fuente, time;
        public final ImageView image;
        public final FrameLayout youLay;

        public ViewHolder(View view){
            super(view);
            ll = (LinearLayout)view.findViewById(R.id.conteiner);
            text = (TextView)view.findViewById(R.id.titulo);
            content = (TextView)view.findViewById(R.id.contenido);
            fuente = (TextView)view.findViewById(R.id.fuente);
            time = (TextView)view.findViewById(R.id.time);
            image = (ImageView)view.findViewById(R.id.image);
            youLay = (FrameLayout)view.findViewById(R.id.youtube);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onItemClick != null)
                onItemClick.onItemClick(v, getPosition());
        }

    }

    public void SetOnItemClickListener(final OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    private int getRandomColor(){
        int color = 0;
        Random r = new Random();
        int aux = r.nextInt(6 - 1) + 1;
        if(aux < colors.length)
            color = colors[aux];
        else
            color = colors[0];
        return color;
    }

}
