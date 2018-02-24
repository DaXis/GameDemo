package com.gamedemo.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gamedemo.R;
import com.gamedemo.SingleTon;
import com.gamedemo.objs.ScheduleObj;

import java.util.ArrayList;

public class ScheduleAdapter extends BaseAdapter {

    private LayoutInflater inflater=null;
    private ArrayList<ScheduleObj> array;

    public ScheduleAdapter(Activity activity){
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        array = SingleTon.getScheduleConfig();
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return array.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.sch_row, parent, false);

        TextView schTxt = ViewHolder.get(convertView, R.id.schTxt);
        schTxt.setText(array.get(position).hint);

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

    public void updateAdapter(){
        this.array = SingleTon.getScheduleConfig();
        notifyDataSetChanged();
    }
}