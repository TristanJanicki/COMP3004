package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;
import com.example.quantrlogin.experiments.MySignals;
import com.google.android.material.navigation.NavigationView;

import networking_handlers.networking_statics;

public class Navigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private LoggedInUser user;
    //private TextView nav_email;
    private TextView nav_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_main);

        user = (LoggedInUser) getIntent().getSerializableExtra("user");

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nav_name = headerView.findViewById(R.id.nav_userName);
        //nav_email = headerView.findViewById(R.id.nav_userEmail);

        nav_name.setText(user.getDisplayName());
        //nav_email.setText(user.getProfileAttribute("user"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.draw_layout);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeAcitvity()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeAcitvity()).commit(); //need to change HomeActivity into a fragment class
                break;

            case R.id.nav_my_signals:
                MySignals signalsFragment = new MySignals();
                signalsFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, signalsFragment).commit(); //need to change MySignals into a fragment class
                break;

            case R.id.nav_calendar:
               // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment()).commit(); //might not work because of "new Fragment()", supposed to be "new MessageFragment()"
                Toast.makeText(this, "Available in a future update.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_td_ameritrade:
                Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();
                redirectTD();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void redirectTD(){
        Intent tdAmer=new Intent(Intent.ACTION_VIEW, Uri.parse(networking_statics.tdaURL));
        startActivity(tdAmer);
    }

}
