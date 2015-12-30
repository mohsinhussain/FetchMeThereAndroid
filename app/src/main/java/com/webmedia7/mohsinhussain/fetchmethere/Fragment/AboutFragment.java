package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.webmedia7.mohsinhussain.fetchmethere.FetchMeThere;
import com.webmedia7.mohsinhussain.fetchmethere.R;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class AboutFragment extends Fragment {

    public AboutFragment(){}
    private SharedPreferences preferenceSettings;
    private SharedPreferences.Editor preferenceEditor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);


        return rootView;
    }

    @Override
    public void onResume() {
        Tracker t = FetchMeThere.getInstance().tracker;
        t.setScreenName("About");
        t.send(new HitBuilders.ScreenViewBuilder().build());
        super.onResume();
    }
}


