package com.gamedemo.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gamedemo.R;

import java.util.ArrayList;

public class ImageFragmentAdapter extends FragmentStatePagerAdapter {

    //commit
    private ArrayList<String> urls = new ArrayList<String>();
    private ArrayList<Integer> views = new ArrayList<Integer>();

    public ImageFragmentAdapter(FragmentManager fm, ArrayList<String> urls) {
        super(fm);
        this.urls = urls;
        setViews();
    }

    @Override
    public Fragment getItem(int position) {
        return ImageFragment.newImageFragment(views.get(position), urls.get(position));
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    private void setViews(){
        views.clear();
        if(!urls.isEmpty()){
            for(int i = 0; i < urls.size(); i++){
                views.add(R.layout.image);
            }
        }
    }

}
