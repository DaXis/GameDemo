package com.gamedemo.fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gamedemo.R;
import com.gamedemo.SingleTon;
import com.gamedemo.objs.UserObj;
import com.gamedemo.utils.ConnectToServer;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoginFragment extends Fragment implements View.OnClickListener{

    private int lay;
    private EditText editUser, editPass;
    private Button loginBtn;
    private TextView forgotPass, newUser;
    private RegisterFragment registerFragment;

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
        SingleTon.getToolbar().setTitle("Login");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_frag, container, false);

        editUser = (EditText)rootView.findViewById(R.id.editUser);
        editPass = (EditText)rootView.findViewById(R.id.editPass);
        editPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    SingleTon.hideKeyboard(LoginFragment.this.getView());
                    checkLogin();
                    handled = true;
                }
                return handled;
            }
        });

        loginBtn = (Button)rootView.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        forgotPass = (TextView)rootView.findViewById(R.id.forgotPass);
        forgotPass.setOnClickListener(this);

        newUser = (TextView)rootView.findViewById(R.id.newUser);
        newUser.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.loginBtn:
                if(editUser.getText().length() > 0){
                    if(editPass.getText().length() > 0){
                        SingleTon.hideKeyboard(LoginFragment.this.getView());
                        checkLogin();
                    } else
                        genToast("Introduce tu contraseña");
                } else
                    genToast("Introduce tu email");
                break;
            case R.id.forgotPass:
                break;
            case R.id.newUser:
                initRegisFrag();
                break;
        }
    }

    private void initRegisFrag(){
        registerFragment = new RegisterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("lay", lay);

        if(registerFragment.getArguments() == null)
            registerFragment.setArguments(bundle);

        SingleTon.setCurrentFragment(registerFragment);

        LoginFragment.this.getFragmentManager().beginTransaction()
                .replace(lay, registerFragment)
                .addToBackStack(null)
                .commit();
    }

    private void genToast(final String msn){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginFragment.this.getActivity(), "" + msn, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkLogin(){
        SingleTon.showLoadDialog(getFragmentManager());
        String url = "http://daxissoft.com/game/check_login.php";
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("email", editUser.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("pass", editPass.getText().toString()));
        ConnectToServer connectToServer = new ConnectToServer(url, nameValuePairs, 3, this);
    }

    public void getResult(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject.getString("status").equals("ok")){
                genToast(jsonObject.getString("message"));

                UserObj userObj = new UserObj();
                userObj.id = jsonObject.getInt("id");
                userObj.email = jsonObject.getString("email");
                userObj.name = jsonObject.getString("name");
                userObj.ap = jsonObject.getString("ap");
                userObj.am = jsonObject.getString("am");
                userObj.alias = jsonObject.getString("alias");
                userObj.phone = jsonObject.getString("phone");
                userObj.image = jsonObject.getString("image");
                userObj.xboxGT = jsonObject.getString("xboxGT");
                userObj.psn = jsonObject.getString("psn");
                userObj.fc = jsonObject.getString("fc");
                SingleTon.setUserObj(userObj);

                if(SingleTon.getUserObj().image != null){
                    if(SingleTon.getUserObj().image.length() > 0){
                        Picasso.with(getActivity()).load(SingleTon.getUserObj().image).into(SingleTon.getProfile());
                        Picasso.with(getActivity()).setIndicatorsEnabled(true);
                    }
                }

                SingleTon.savePreferences("login", true);
                SingleTon.savePreferences("json_login", json);
                SingleTon.getUsername().setText(SingleTon.getUserObj().alias);
                SingleTon.getEmail().setText(SingleTon.getUserObj().email);
                SingleTon.getEmail().setVisibility(View.VISIBLE);
                SingleTon.getLogin().setTitle("Cerrar Sesión");

                getActivity().onBackPressed();
            } else
                genToast(jsonObject.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SingleTon.dissmissLoad();
    }

}
