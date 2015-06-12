package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webmedia7.mohsinhussain.fetchmethere.R;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class FavouriteBusinessFragment extends Fragment {

    public FavouriteBusinessFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favourite_businesses, container, false);

        return rootView;
    }
}
