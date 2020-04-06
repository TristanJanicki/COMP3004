package com.example.quantrlogin.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.dbmodels.CorrelationExperiment;
import com.example.quantrlogin.data.dbmodels.Experiment;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;
import com.example.quantrlogin.data.dbmodels.ThresholdExperiment;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import networking_handlers.GetExperimentsHandler;

public class CalendarPage extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LoggedInUser user;

    public CalendarPage() {
        // Required empty public constructor
    }
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar_page, container, false);

        user = (LoggedInUser) getActivity().getIntent().getSerializableExtra("user");

        getSignals(user);

        //Must Get Dark Mode to work
        //checkDarkMode = LoginActivity.getDarkMode();
        //updateDarkMode(view);



        ArrayList<CalItem> calList = new ArrayList<>();


        for (Experiment e : user.experiments) {
            if (e instanceof ThresholdExperiment){
                ThresholdExperiment t = (ThresholdExperiment) e;

                String[] dates = t.getEvent_dates();

                for(int i = 0; i < Math.min(dates.length, 10); i++){
                    int random = ThreadLocalRandom.current().nextInt(1, 4);
                    if (random == 1) {
                        calList.add(new CalItem(R.drawable.ic_emai, dates[i], t.getTicker() + " " + t.getIndicator() + " " + t.getThreshold()));
                    } else if (random == 2) {
                        calList.add(new CalItem(R.drawable.ic_notifications_active_black_24dp, dates[i], t.getTicker() + " " + t.getIndicator() + " " + t.getThreshold()));
                    } else {
                        calList.add(new CalItem(R.drawable.ic_twitter, dates[i], t.getTicker() + " " + t.getIndicator() + " " + t.getThreshold()));
                    }
                }

            }
        }

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mAdapter = new CalAdapater(calList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private Experiment[] getSignals(LoggedInUser user){
        GetExperimentsHandler geh = new GetExperimentsHandler();
        Logger.getGlobal().warning("ABOUT TO EXECUTE GET EXPERIMENTS HANLDER");
        geh.execute(user);
        try {
            Result r = geh.get();
            if (r instanceof Result.GetExperimentsResult){
                CorrelationExperiment[] corrs = ((Result.GetExperimentsResult) r).getCorrelationExperiments();
                ThresholdExperiment[] threshs = ((Result.GetExperimentsResult) r).getThresholdExperiments();
                Experiment[] allExperiments = new Experiment[corrs.length + threshs.length];

                int i = 0;
                for(CorrelationExperiment c : corrs){
                    allExperiments[i] = c;
                    ++i;
                }
                for(ThresholdExperiment t : threshs){
                    allExperiments[i] = t;
                    ++i;
                }
                user.experiments = allExperiments;
                return allExperiments;
            }else{
                Logger.getGlobal().warning("NOT SUCCESS: "+ r.toString());
                return new Experiment[]{};
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return new Experiment[]{};
    }
}
