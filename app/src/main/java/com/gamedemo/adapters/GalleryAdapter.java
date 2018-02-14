package com.gamedemo.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.gamedemo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> urls;
    private LayoutInflater inflater=null;

    public GalleryAdapter(Context context, ArrayList<String> urls){
        this.context = context;
        this.urls = urls;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView img = null;
        ProgressBar pb = null;

        if(convertView == null)
            convertView = inflater.inflate(R.layout.gall, parent, false);

        img = ViewHolder.get(convertView, R.id.gallImageView);
        pb = ViewHolder.get(convertView, R.id.gallProgressBar);

        //Memory.loadImage(urls.get(position), img, pb);
        Picasso.with(context).load(urls.get(position)).into(img);

        return convertView;
    }

    public static class ViewHolder {
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }

}
