package com.gamedemo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gamedemo.R;
import com.gamedemo.SingleTon;
import com.gamedemo.dialogs.DownloadDialog;
import com.gamedemo.dialogs.ScheduleDialog;
import com.gamedemo.objs.ScheduleObj;
import com.gamedemo.utils.DownloadHelper;

public class ConfigFragment extends Fragment implements View.OnClickListener{

    private int lay;
    private TextView schedule;
    private FrameLayout atomix_check_lay, lvup_check_lay;
    private CheckBox atomix_check, lvup_check;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
        SingleTon.getToolbar().setTitle("Configuración");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.config_fragment, container, false);

        schedule = (TextView)rootView.findViewById(R.id.schedule);
        schedule.setOnClickListener(this);

        atomix_check_lay = (FrameLayout)rootView.findViewById(R.id.atomix_check_lay);
        //atomix_check_lay.setOnClickListener(this);
        lvup_check_lay = (FrameLayout)rootView.findViewById(R.id.lvup_check_lay);
        //lvup_check_lay.setOnClickListener(this);

        atomix_check = (CheckBox)rootView.findViewById(R.id.atomix_check);
        atomix_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SingleTon.savePreferences("atomix_check", isChecked);
            }
        });

        lvup_check = (CheckBox)rootView.findViewById(R.id.lvup_check);
        lvup_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SingleTon.savePreferences("lvup_check", isChecked);
            }
        });

        boolean atomix_check_b = SingleTon.getSettings().getBoolean("atomix_check", true),
                lvup_check_b = SingleTon.getSettings().getBoolean("lvup_check", true);
        atomix_check.setChecked(atomix_check_b);
        lvup_check.setChecked(lvup_check_b);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.schedule:
                scheduleDialog();
                break;
            case R.id.lvup_check_lay:
                if(lvup_check.isChecked() && atomix_check.isChecked())
                    lvup_check.setChecked(false);
                else
                    lvup_check.setChecked(true);
                break;
            case R.id.atomix_check_lay:
                if(atomix_check.isChecked() && lvup_check.isChecked())
                    atomix_check.setChecked(false);
                else
                    atomix_check.setChecked(true);
                break;
        }
    }

    private void scheduleDialog(){
        ScheduleDialog scheduleDialog = ScheduleDialog.newInstance();
        scheduleDialog.setCancelable(true);
        scheduleDialog.show(getFragmentManager(), "schedule dialog");
    }

    public void changeSchedule(final ScheduleObj arg){
        SingleTon.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                schedule.setText(arg.hint);
            }
        });
    }

}
