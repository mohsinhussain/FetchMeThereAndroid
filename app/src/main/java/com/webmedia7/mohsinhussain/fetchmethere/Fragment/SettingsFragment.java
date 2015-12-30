package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.FetchMeThere;
import com.webmedia7.mohsinhussain.fetchmethere.R;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class SettingsFragment extends Fragment {

    public SettingsFragment(){}
    private SharedPreferences preferenceSettings;
    private SharedPreferences.Editor preferenceEditor;

    private String unit = "";
    private String voice = "";

    private Switch unitSwitch, voiceSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        unitSwitch = (Switch) rootView.findViewById(R.id.unitSwitch);
        voiceSwitch = (Switch) rootView.findViewById(R.id.voiceSwitch);

        preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
        unit = preferenceSettings.getString("unit", null);
        voice = preferenceSettings.getString("voice", null);

        if(unit.equalsIgnoreCase("kms")){
            unitSwitch.setChecked(true);
        }
        else{
            unitSwitch.setChecked(false);
        }


        if(voice.equalsIgnoreCase("on")){
            voiceSwitch.setChecked(true);
        }
        else{
            voiceSwitch.setChecked(false);
        }

        unitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
                    preferenceEditor = preferenceSettings.edit();
                    preferenceEditor.putString("unit", "kms");
                    preferenceEditor.commit();
                }else{
                    preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
                    preferenceEditor = preferenceSettings.edit();
                    preferenceEditor.putString("unit", "miles");
                    preferenceEditor.commit();
                }
            }
        });

        voiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
                    preferenceEditor = preferenceSettings.edit();
                    preferenceEditor.putString("voice", "on");
                    preferenceEditor.commit();
                }else{
                    preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
                    preferenceEditor = preferenceSettings.edit();
                    preferenceEditor.putString("voice", "off");
                    preferenceEditor.commit();
                }
            }
        });




        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Tracker t = FetchMeThere.getInstance().tracker;
        t.setScreenName("Settings");
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }
}


