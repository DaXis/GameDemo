package com.gamedemo.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gamedemo.R;
import com.gamedemo.SingleTon;
import com.gamedemo.adapters.ScheduleAdapter;
import com.gamedemo.fragments.ConfigFragment;
import com.gamedemo.objs.ScheduleObj;

public class ScheduleDialog extends DialogFragment {

    private ListView scheduleList;

    public static ScheduleDialog newInstance(){
        ScheduleDialog scheduleDialog = new ScheduleDialog();

        return scheduleDialog;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo;
        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.schedule, null);
        builder.setView(v);

        scheduleList = (ListView)v.findViewById(R.id.scheduleList);
        final ScheduleAdapter adapter = new ScheduleAdapter(SingleTon.getCurrentActivity());
        scheduleList.setAdapter(adapter);
        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ConfigFragment)SingleTon.getCurrentFragment()).changeSchedule((ScheduleObj) adapter.getItem(position));
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.HONEYCOMB){
            Log.e("solved super error", "solved super error OK");
        } else
            super.onSaveInstanceState(outState);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

}
