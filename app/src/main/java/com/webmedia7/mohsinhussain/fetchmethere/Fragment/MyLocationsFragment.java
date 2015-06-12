package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.webmedia7.mohsinhussain.fetchmethere.Adapters.MyLocationsAdapter;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Model.Location;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.util.ArrayList;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class MyLocationsFragment extends Fragment {

    public MyLocationsFragment(){}

    private OnFragmentInteractionListener mListener;

    ListView locationsListView;
    SharedPreferences preferenceSettings;
    String userId = "";
    ArrayList<Location> locationArrayList = new ArrayList<Location>();
    ArrayList<String> imagesArray = new ArrayList<String>();
    MyLocationsAdapter mAdapter;
    ProgressDialog ringProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_locations, container, false);

        preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
        userId = preferenceSettings.getString("userId", null);

        locationsListView = (ListView) rootView.findViewById(R.id.location_list_view);

        mAdapter = new MyLocationsAdapter(getActivity(), locationArrayList);

        locationsListView.setAdapter(mAdapter);


        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Finding Locations...", true);
        ringProgressDialog.setCancelable(true);

        // Get a reference to our posts
        final Firebase ref = new Firebase(Constants.BASE_URL);
        Firebase postRef = ref.child("users").child(userId).child("myLocations");


        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot);

                locationArrayList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Location location = new Location();
                    location.setRefId(child.getKey());
                    System.out.println("KEY: " + child.getKey());
                    System.out.println("VALUE: " + child.getValue());
                    for (DataSnapshot mChild : child.getChildren()){
                        if (mChild.getKey().equalsIgnoreCase("Address")){
                            location.setAddress(mChild.getValue().toString());
                        }
                        else if (mChild.getKey().equalsIgnoreCase("Name")){
                            location.setName(mChild.getValue().toString());
                        }
                        else if (mChild.getKey().equalsIgnoreCase("lang")){
                            location.setLongitude(Double.parseDouble(mChild.getValue().toString()));
                        }
                        else if (mChild.getKey().equalsIgnoreCase("lat")){
                            location.setLatitude(Double.parseDouble(mChild.getValue().toString()));
                        }
                        else if (mChild.getKey().equalsIgnoreCase("imagesArray")){
                            location.setImagesArray((ArrayList<String>) mChild.getValue());
                        }
                    }
                    locationArrayList.add(location);
                }

                ringProgressDialog.dismiss();

                mAdapter.mHighlightedPositions = new boolean[locationArrayList.size()];
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu.hasVisibleItems()){
            super.onCreateOptionsMenu(menu, inflater);
        }
        else{
            inflater.inflate(R.menu.menu_mylocations_fragment, menu);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_location:
            {
                if (mListener!=null){
                    mListener.onActionAddLocation();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onActionAddLocation();
    }
}
