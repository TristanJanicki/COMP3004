package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONException;

import java.util.ArrayList;

import networking_handlers.networking_statics;

public class HomeAcitvity extends Fragment {

    private LoggedInUser user;
    private LineChart lineChart;
    private ConstraintLayout homeLayout;
    private Switch dark_mode;
    private boolean checkDarkMode;
    private TextView username, email, notifsLabel;
    private boolean loggedIn = false;
    public static boolean profileDarkModeOn = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_home, container, false);
        user = (LoggedInUser) getActivity().getIntent().getSerializableExtra("user");

        homeLayout = view.findViewById(R.id.home_relativeLayout);
        username = view.findViewById(R.id.profile_name);
        email = view.findViewById(R.id.profile_email);
        dark_mode = view.findViewById(R.id.profile_dark_mode);
        notifsLabel = view.findViewById(R.id.subscribeToNotifsTextView);
        //set username text of user
        email.setText(user.getDisplayName());

        //set email text of user
        try {
            username.setText(user.getProfileAttribute("name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setDarkMode();

        dark_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDarkMode();
            }
        });


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void redirectTD(View view){
        Intent tdAmer=new Intent(Intent.ACTION_VIEW, Uri.parse(networking_statics.tdaURL));
        startActivity(tdAmer);
    }

    public static Boolean getDarkMode(){
        return profileDarkModeOn;
    }

    public void setDarkMode(){
        if (dark_mode.isChecked()) { //if in light mode
            //make necessary changes to convert to dark mode
            profileDarkModeOn = true;
            homeLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.profile_background_dark));
//            username.setTextColor(getResources().getColor(R.color.LightGrey));
//            email.setTextColor(getResources().getColor(R.color.LightGrey));
            notifsLabel.setTextColor(getResources().getColor(R.color.White));
        } else { //else in dark mode
            //make necessary changes to convert to dark mode
            profileDarkModeOn = false;
            homeLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.profile_background_light));
            notifsLabel.setTextColor(Color.BLACK);

            username.setTextColor(getResources().getColor(R.color.Grey));
            email.setTextColor(getResources().getColor(R.color.Grey));
        }
    }

    private ArrayList getData(){
        ArrayList<Entry> entries = new ArrayList<>();

        entries.add(new Entry(0f, 4f));
        entries.add(new Entry(1f, 1f));
        entries.add(new Entry(2f, 2f));
        entries.add(new Entry(3f, 4f));

        return entries;
    }

//    private void startNewSignalActivity(LoggedInUser user){
//        Intent intent = new Intent(this, NewSignal.class);
//        intent.putExtra("user", user);
//        startActivity(intent);
//    }
}