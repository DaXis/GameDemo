package com.gamedemo;

import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gamedemo.dialogs.LoadDialog;
import com.gamedemo.fragments.ConfigFragment;
import com.gamedemo.fragments.HomeFragment;
import com.gamedemo.fragments.LoginFragment;
import com.gamedemo.objs.UserObj;
import com.gamedemo.utils.ConnectToServer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FrameLayout contenLay;
    private HomeFragment homeFragment;
    private ConfigFragment configFragment;
    private LoginFragment loginFragment;
    private CircleImageView profile;
    private TextView username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();
        initView();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SingleTon.setToolbar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    private void initView(){
        homeFragment = new HomeFragment();
        configFragment = new ConfigFragment();
        loginFragment = new LoginFragment();

        contenLay = (FrameLayout)findViewById(R.id.mainContent);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        setupDrawerContent(navigationView);
        MenuItem menuItem = null;
        for(int i = 0; i < navigationView.getMenu().size(); i++){
            menuItem = navigationView.getMenu().getItem(i);
            Log.d("menu item "+i, menuItem.getTitle().toString());
            if(menuItem.getItemId() == R.id.configuration_section) {
                break;
            }
        }

        for(int i = 0; i <  menuItem.getSubMenu().size(); i++){
            MenuItem menuItem0 =  menuItem.getSubMenu().getItem(i);
            if(menuItem0.getItemId() == R.id.nav_log) {
                SingleTon.setLogin(menuItem0);
                break;
            }
        }

        View view = navigationView.getHeaderView(0);
        profile = (CircleImageView)view.findViewById(R.id.circle_image);
        username = (TextView)view.findViewById(R.id.username);
        email = (TextView)view.findViewById(R.id.email);
        SingleTon.setProfile(profile);
        SingleTon.setUsername(username);
        SingleTon.setEmail(email);

        if(SingleTon.getUserObj() != null){
            if(SingleTon.getUserObj().image.length() > 0){
                Picasso.with(this).load(SingleTon.getUserObj().image).into(profile);
                Picasso.with(this).setIndicatorsEnabled(true);
            }
            username.setText(SingleTon.getUserObj().username);
            email.setText(SingleTon.getUserObj().email);
            email.setVisibility(View.VISIBLE);
            SingleTon.getLogin().setTitle("Cerrar Sesión");
        } else {
            email.setVisibility(View.INVISIBLE);
            email.setText("");
            profile.setImageResource(R.drawable.perfil);
            username.setText("Invitado");
            SingleTon.getLogin().setTitle("Iniciar Sesión");
        }

        initHomeFragment();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.config){
                            drawerLayout.closeDrawer(GravityCompat.START);
                            initConfigFragment();
                        }
                        if(menuItem.getItemId() == R.id.nav_log){
                            drawerLayout.closeDrawer(GravityCompat.START);
                            if(SingleTon.getUserObj() == null)
                                initLoginFragment();
                            else
                                logout();
                        }
                        return true;
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeFragments(){
        if(SingleTon.getCurrentFragment() != null){
            Log.d("fragment", SingleTon.getCurrentFragment().getClass().toString());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(SingleTon.getCurrentFragment()).commit();
        }
    }

    private void initHomeFragment(){
        if(SingleTon.getCurrentFragment() != homeFragment){
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", contenLay.getId());
            if(homeFragment.getArguments() == null)
                homeFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(contenLay.getId(), homeFragment).commit();
        }
    }

    private void initConfigFragment(){
        if(SingleTon.getCurrentFragment() != configFragment){
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", contenLay.getId());
            if(configFragment.getArguments() == null)
                configFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(contenLay.getId(), configFragment).commit();
        }
    }

    private void initLoginFragment(){
        if(SingleTon.getCurrentFragment() != loginFragment){
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", contenLay.getId());
            if(loginFragment.getArguments() == null)
                loginFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(contenLay.getId(), loginFragment).commit();
        }
    }

    private void genToast(final String msn){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "" + msn, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logout(){
        SingleTon.getEditor().putBoolean("login", false);
        SingleTon.getEditor().putString("json_login","");
        SingleTon.getEditor().commit();
        SingleTon.getLogin().setTitle("Iniciar Sesión");
        email.setVisibility(View.INVISIBLE);
        email.setText("");
        profile.setImageResource(R.drawable.perfil);
        username.setText("Invitado");

        genToast(SingleTon.getUserObj().username+" a cerrado sesión.");

        SingleTon.setUserObj(null);
    }

    @Override
    public void onBackPressed(){
        if(SingleTon.getCurrentFragment() == loginFragment)
            initHomeFragment();
        else if(SingleTon.getCurrentFragment() == configFragment)
            initHomeFragment();
        else
            super.onBackPressed();
    }

}
