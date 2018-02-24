package com.gamedemo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamedemo.R;
import com.gamedemo.SingleTon;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private int lay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
        SingleTon.getToolbar().setTitle("Perfil");
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
        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

        }
    }

}
