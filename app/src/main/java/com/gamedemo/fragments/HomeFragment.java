package com.gamedemo.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamedemo.R;
import com.gamedemo.SingleTon;
import com.gamedemo.adapters.HomeRecyclerAdapter;
import com.gamedemo.adapters.OnItemClick;
import com.gamedemo.objs.HomeObj;
import com.gamedemo.utils.ConnectToServer;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeRecyclerAdapter adapter;
    private ArrayList<HomeObj> homeObjs = new ArrayList<>();
    private int lay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
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
        View rootView = inflater.inflate(R.layout.home_frag, container, false);

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.homeList);
        adapter = new HomeRecyclerAdapter(getActivity(), homeObjs, getFragmentManager());
        adapter.SetOnItemClickListener(new OnItemClick() {
            @Override
            public void onItemClick(View view, int position) {
                initNoteFragment(adapter.getItem(position).urlNote);
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        SingleTon.showLoadDialog(this.getFragmentManager());
        String[] urls = {"http://atomix.vg/", "http://www.levelup.com/noticias"};
        Object[] args = {urls,0,this};
        ConnectToServer connectToServer = new ConnectToServer(args);

        return rootView;
    }

    public void getResult(){
        Cursor c = SingleTon.getDb().rawQuery("SELECT titulo,resumen,urlNote,urlImg,color,backImg,urlYtb,date,rootPage,dateLong"
                + " FROM Home ORDER BY dateLong DESC", null);
        if(c.moveToFirst()){
            do{
                HomeObj hn = new HomeObj();
                hn.titulo = c.getString(0);
                hn.resumen = c.getString(1);
                hn.urlNote = c.getString(2);
                hn.urlImg = c.getString(3);
                hn.color = c.getString(4);
                hn.backImg = c.getString(5);
                hn.urlYtb = c.getString(6);
                hn.date = c.getString(7);
                hn.rootPage = c.getString(8);
                hn.dateLong = c.getLong(9);
                homeObjs.add(hn);
            } while(c.moveToNext());
        }
        c.close();
        adapter.updateAdapter(homeObjs);
        SingleTon.dissmissLoad();
    }

    private void initNoteFragment(String url){
        Bundle bundle = new Bundle();
        bundle.putInt("lay", lay);
        bundle.putString("url", url);
        NoteFragment noteFragment = new NoteFragment();
        noteFragment.setArguments(bundle);
        SingleTon.setCurrentFragment(noteFragment);
        HomeFragment.this.getFragmentManager().beginTransaction()
                .replace(lay, noteFragment)
                .addToBackStack(null)
                .commit();
    }

}
