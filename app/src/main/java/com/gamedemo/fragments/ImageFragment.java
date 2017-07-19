package com.gamedemo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.gamedemo.R;
import com.squareup.picasso.Picasso;

public class ImageFragment extends Fragment {

    private int content;
    private String url;
    private View view;

    public static ImageFragment newImageFragment(int content, String url){
        ImageFragment imageFragment = new ImageFragment();
        imageFragment.content = content;
        imageFragment.url = url;

        return imageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        if(view == null)
            view = inflater.inflate(content, container, false);

        ImageView image = (ImageView)view.findViewById(R.id.img_full);
        Picasso.with(getActivity()).load(url).into(image);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

}
