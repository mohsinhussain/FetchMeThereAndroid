package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.webmedia7.mohsinhussain.fetchmethere.Adapters.NavHistoryAdapter;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Model.NavigationHistory;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class NavigationHistoryListFragment extends Fragment {

    public NavigationHistoryListFragment(){}

    ListView navListView;
    SharedPreferences preferenceSettings;
    String userId = "";
    ArrayList<NavigationHistory> navArrayList = new ArrayList<NavigationHistory>();
    NavHistoryAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_navigation_history_list, container, false);

        preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
        userId = preferenceSettings.getString("userId", null);

        navListView = (ListView) rootView.findViewById(R.id.nav_list_view);

        mAdapter = new NavHistoryAdapter(getActivity(), navArrayList);

        navListView.setAdapter(mAdapter);

        setHasOptionsMenu(false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get a reference to our posts
        final Firebase ref = new Firebase(Constants.BASE_URL);
        Firebase postRef = ref.child("users").child(userId).child("navigationHistory");


        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot);

                navArrayList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    NavigationHistory navHistory = new NavigationHistory();
                    navHistory.setId(child.getKey());
                    System.out.println("KEY: " + child.getKey());
                    System.out.println("VALUE: " + child.getValue());
                    for (DataSnapshot mChild : child.getChildren()) {
                        if (mChild.getKey().equalsIgnoreCase("action")) {
                            navHistory.setAction(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("distanceCovered")) {
                            navHistory.setDistanceCovered(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("friendAddress")) {
                            navHistory.setFriendAddress(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("friendId")) {
                            navHistory.setFriendId(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("friendLang")) {
                            navHistory.setFriendLang(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("friendLat")) {
                            navHistory.setFriendLat(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("friendLocName")) {
                            navHistory.setFriendLocName(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("friendName")) {
                            navHistory.setFriendName(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("myAddress")) {
                            navHistory.setMyAddress(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("myDisplayName")) {
                            navHistory.setMyDisplayName(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("myLang")) {
                            navHistory.setMyLang(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("myLat")) {
                            navHistory.setMyLat(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("myUserId")) {
                            navHistory.setMyUserId(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("navStartTime")) {
                            navHistory.setNavStartTime(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("navEndTime")) {
                            navHistory.setNavEndTime(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("status")) {
                            navHistory.setStatus(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("totalDistance")) {
                            navHistory.setTotalDistance(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("totalDuration")) {
                            navHistory.setTotalDuration(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("profileImageString")) {
                            navHistory.setFriendProfileImageString(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("mode")) {
                            navHistory.setMode(mChild.getValue().toString());
                        }

                    }
                    navArrayList.add(navHistory);
                }
                Collections.reverse(navArrayList);

                mAdapter.mHighlightedPositions = new boolean[navArrayList.size()];
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}
