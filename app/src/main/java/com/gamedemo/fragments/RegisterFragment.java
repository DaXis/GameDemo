package com.gamedemo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gamedemo.R;
import com.gamedemo.SingleTon;
import com.gamedemo.utils.ConnectToServer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegisterFragment extends Fragment implements View.OnClickListener{

    private int lay;
    private EditText email, name, apPa, apMa, nick, tel, editPass, confirmPass;
    private Button regis;

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
        SingleTon.getToolbar().setTitle("Registrate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.regis_frag, container, false);

        email = (EditText)rootView.findViewById(R.id.email);
        name = (EditText)rootView.findViewById(R.id.name);
        apPa = (EditText)rootView.findViewById(R.id.apPa);
        apMa = (EditText)rootView.findViewById(R.id.apMa);
        nick = (EditText)rootView.findViewById(R.id.nick);
        tel = (EditText)rootView.findViewById(R.id.tel);
        editPass = (EditText)rootView.findViewById(R.id.editPass);
        confirmPass = (EditText)rootView.findViewById(R.id.confirmPass);

        regis = (Button)rootView.findViewById(R.id.regisBtn);
        regis.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.regisBtn:
                if(email.getText().length() > 0){
                    if(nick.getText().length() > 0){
                        if(editPass.getText().length() > 0){
                            if(confirmPass.getText().toString().equals(editPass.getText().toString())){
                                initConnection();
                            } else
                                genToast("Las contraseñas no coinciden");
                        } else
                            genToast("Debes escribir una contraseña.");
                    } else
                        genToast("Debes escoger un Alias.");
                } else
                    genToast("Debes introducir tu eMail.");
                break;
        }
    }

    private void initConnection(){
        SingleTon.showLoadDialog(getFragmentManager());

        String url = "http://daxissoft.com/game/insert_user.php";
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("email", email.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("name", name.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("ap", apPa.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("am", apMa.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("alias", nick.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("phone", tel.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("pass", editPass.getText().toString()));

        ConnectToServer connectToServer = new ConnectToServer(url, nameValuePairs, 2, this);
    }

    public void getResult(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject.getString("status").equals("ok")){
                genToast(jsonObject.getString("message"));
                SingleTon.getCurrentActivity().onBackPressed();
            } else
                genToast(jsonObject.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SingleTon.dissmissLoad();
    }

    private void genToast(final String msn){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterFragment.this.getActivity(), "" + msn, Toast.LENGTH_LONG).show();
            }
        });
    }
}
