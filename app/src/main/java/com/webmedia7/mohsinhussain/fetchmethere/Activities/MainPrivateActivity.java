package com.webmedia7.mohsinhussain.fetchmethere.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.webmedia7.mohsinhussain.fetchmethere.Adapters.NavDrawerListAdapter;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Fragment.AboutFragment;
import com.webmedia7.mohsinhussain.fetchmethere.Fragment.AddEditLocationFragment;
import com.webmedia7.mohsinhussain.fetchmethere.Fragment.ChatFragment;
import com.webmedia7.mohsinhussain.fetchmethere.Fragment.ContactsFragment;
import com.webmedia7.mohsinhussain.fetchmethere.Fragment.HomePrivateFragment;
import com.webmedia7.mohsinhussain.fetchmethere.Fragment.LocationDetailFragment;
import com.webmedia7.mohsinhussain.fetchmethere.Fragment.MyLocationsFragment;
import com.webmedia7.mohsinhussain.fetchmethere.Fragment.NavigationFragment;
import com.webmedia7.mohsinhussain.fetchmethere.Fragment.NavigationHistoryDetailFragment;
import com.webmedia7.mohsinhussain.fetchmethere.Fragment.NavigationHistoryListFragment;
import com.webmedia7.mohsinhussain.fetchmethere.Fragment.ProfileFragment;
import com.webmedia7.mohsinhussain.fetchmethere.Fragment.SettingsFragment;
import com.webmedia7.mohsinhussain.fetchmethere.Model.NavDrawerItem;
import com.webmedia7.mohsinhussain.fetchmethere.Model.NavigationHistory;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.util.ArrayList;

public class MainPrivateActivity extends ActionBarActivity implements HomePrivateFragment.OnFragmentInteractionListener, MyLocationsFragment.OnFragmentInteractionListener, LocationDetailFragment.OnFragmentInteractionListener, ContactsFragment.OnFragmentInteractionListener

{

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private SharedPreferences preferenceSettings;
    private SharedPreferences.Editor preferenceEditor;

    private String displayName;
    private String userId;
    private String profileImageString;
    String locationSentMessage = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main_private);
        if(!Firebase.getDefaultConfig().isPersistenceEnabled()){
            Firebase.getDefaultConfig().setPersistenceEnabled(true);
        }
        Firebase.setAndroidContext(this);



        preferenceSettings = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        displayName = preferenceSettings.getString("displayName", null);
        userId = preferenceSettings.getString("userId", null);
        profileImageString = preferenceSettings.getString("profileImageString", null);

        if(!preferenceSettings.contains("voice")){
            preferenceSettings = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
            preferenceEditor = preferenceSettings.edit();
            preferenceEditor.putString("voice", "on");
            preferenceEditor.commit();
        }

        if(!preferenceSettings.contains("units")){
            preferenceSettings = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
            preferenceEditor = preferenceSettings.edit();
            preferenceEditor.putString("unit", "kms");
            preferenceEditor.commit();
        }
        Log.v("HELOO",displayName);


        mTitle = getTitle();
        mDrawerTitle = "FetchMeThere";

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        //adding profile details item
        navDrawerItems.add(new NavDrawerItem(true, profileImageString,displayName , "0", "0"));

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // What's hot, We  will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));


        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.nav_main_menu_bg);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.nav_main_menu_bg, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(1);
        }

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());


    }

    @Override
    protected void onStart() {
        super.onStart();
        final Firebase ref = new Firebase(Constants.BASE_URL);
        final Firebase myStatusRef = ref.child("users").child(userId).child("status");
        myStatusRef.setValue("online");
    }


    @Override
    protected void onStop() {
        super.onStop();
        final Firebase ref = new Firebase(Constants.BASE_URL);
        final Firebase myStatusRef = ref.child("users").child(userId).child("status");
        myStatusRef.setValue("offline");
    }


    public void onDeleteNavigationHistory(NavigationHistory navHistory){
        System.out.println("NavId: "+navHistory.getId());
        final Firebase ref = new Firebase(Constants.BASE_URL);
        final Firebase navRef = ref.child("users").child(userId).child("navigationHistory").child(navHistory.getId());
        navRef.setValue(null);
    }

    public void onLoadNavigationHistoryDetail(NavigationHistory navHistory){
        Fragment navHistoryDetailFragment = new NavigationHistoryDetailFragment();
        Bundle bndle = new Bundle();
        bndle.putString("action", navHistory.getAction());
        bndle.putString("distanceCovered", navHistory.getDistanceCovered());
        bndle.putString("friendAddress", navHistory.getFriendAddress());
        bndle.putString("friendId", navHistory.getFriendId());
        bndle.putString("friendLang", navHistory.getFriendLang());
        bndle.putString("friendLat", navHistory.getFriendLat());
        bndle.putString("friendLocName", navHistory.getFriendLocName());
        bndle.putString("friendName", navHistory.getFriendName());
        bndle.putString("profileImageString", navHistory.getFriendProfileImageString());
        bndle.putString("myAddress", navHistory.getMyAddress());
        bndle.putString("myDisplayName", navHistory.getMyDisplayName());
        bndle.putString("myLang", navHistory.getMyLang());
        bndle.putString("myLat", navHistory.getMyLat());
        bndle.putString("myUserId", navHistory.getMyUserId());
        bndle.putString("navStartTime", navHistory.getNavStartTime());
        bndle.putString("navEndTime", navHistory.getNavEndTime());
        bndle.putString("status", navHistory.getStatus());
        bndle.putString("totalDistance", navHistory.getTotalDistance());
        bndle.putString("totalDuration", navHistory.getTotalDuration());
        bndle.putString("mode", navHistory.getMode());
        navHistoryDetailFragment.setArguments(bndle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, navHistoryDetailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


    public void onSeeMore(String name, String address, double lat, double lang, ArrayList<String> imagesArray, String refId){
        Fragment locationDetailFragment = new LocationDetailFragment();
        Bundle bndle = new Bundle();
        bndle.putString("name", name);
        bndle.putString("address", address);
        bndle.putDouble("lat", lat);
        bndle.putDouble("lang", lang);
        bndle.putString("refId", refId);
        bndle.putString("action", "detail");
        bndle.putStringArrayList("imagesArray", imagesArray);
        locationDetailFragment.setArguments(bndle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, locationDetailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onMyLocationsClicked() {
        setTitle("My Locations");
        Fragment myLocationsFragment = new MyLocationsFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, myLocationsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onRequestLocation() {
        setTitle("Request Location");
        Fragment contactsFragment = new ContactsFragment();
        Bundle bndle = new Bundle();
        bndle.putString("action", "requestLocation");
        contactsFragment.setArguments(bndle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, contactsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onSendLocationClicked(double lat, double lang) {
        setTitle("Send Location");
        Fragment contactsFragment = new ContactsFragment();
        Bundle bndle = new Bundle();
        bndle.putDouble("lat", lat);
        bndle.putDouble("lang", lang);
        bndle.putString("action", "sendLocation");
        contactsFragment.setArguments(bndle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, contactsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();






    }

    @Override
    public void onBusinessesClicked() {
        final Dialog dialog = new Dialog(MainPrivateActivity.this, R.style.mydialogstyle);
        dialog.setContentView(R.layout.dialog_business_coming_soon);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onStartNavigation(String friendId, String friendName, String locationName, String lat, String lang, String address, double currentLat, double currentLang, String profileImageString) {
        setTitle("Navigation");
        Fragment navFragment = new NavigationFragment();
        Bundle bndle = new Bundle();
        bndle.putString("friendId", friendId);
        bndle.putString("friendName", friendName);
        bndle.putString("address", address);
        bndle.putString("locationName", locationName);
        bndle.putString("lat", lat);
        bndle.putString("lang", lang);
        bndle.putDouble("currentLat", currentLat);
        bndle.putDouble("currentLang", currentLang);
        bndle.putString("profileImageString", profileImageString);
        bndle.putString("action", "object");
        navFragment.setArguments(bndle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, navFragment, "navFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onActionAddLocation() {
        setTitle("Add Location");
        Fragment addLocationsFragment = new AddEditLocationFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, addLocationsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onSendLocationToFriend(String friendId, String friendName, String profileImageString, double lat, double lang) {
        setTitle("Send Location");
        Fragment contactsFragment = new LocationDetailFragment();
        Bundle bndle = new Bundle();
        bndle.putString("friendId", friendId);
        bndle.putString("friendName", friendName);
        bndle.putDouble("lat", lat);
        bndle.putDouble("lang", lang);
        bndle.putString("profileImageString", profileImageString);
        bndle.putString("action", "sendCurrentLocation");
        contactsFragment.setArguments(bndle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, contactsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onChatToFriend(String friendId, String friendName, String friendProfileImageString) {
        setTitle(friendName);
        Fragment chatFragment = new ChatFragment();
        Bundle bndle = new Bundle();
        bndle.putString("friendId", friendId);
        bndle.putString("friendName", friendName);
        bndle.putString("profileImageString", friendProfileImageString);
        bndle.putString("action", "chat");
        chatFragment.setArguments(bndle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, chatFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onRequestLocation(String friendId, String friendName, String profileImageView) {
        setTitle("Request Location");
        Fragment contactsFragment = new LocationDetailFragment();
        Bundle bndle = new Bundle();
        bndle.putString("friendId", friendId);
        bndle.putString("friendName", friendName);
        bndle.putString("profileImageString", profileImageView);
        bndle.putString("action", "requestLocation");
        contactsFragment.setArguments(bndle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, contactsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onEditLocation(String name, String address, double lat, double lang, ArrayList<String> imagesArray, String refId) {
        Fragment editLocationsFragment = new AddEditLocationFragment();
        Bundle bndle = new Bundle();
        bndle.putString("name", name);
        bndle.putString("address", address);
        bndle.putDouble("lat", lat);
        bndle.putDouble("lang", lang);
        bndle.putString("refId", refId);
        bndle.putStringArrayList("imagesArray", imagesArray);
        bndle.putString("action", "edit");
        editLocationsFragment.setArguments(bndle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, editLocationsFragment, "editLocation");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onSendSavedLocation(String locName, String address, String lat, String lang) {
        Fragment contactsFragment = new ContactsFragment();
        Bundle bndle = new Bundle();
        bndle.putString("locName", locName);
        bndle.putString("address", address);
        bndle.putString("lat", lat);
        bndle.putString("lang", lang);
        bndle.putString("profileImageString", profileImageString);
        bndle.putString("action", "sendSavedLocation");
        contactsFragment.setArguments(bndle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, contactsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onLocationSent(String friendName, String locationName) {
        locationSentMessage = "Your location is successfully sent to "+friendName+". Acceptence is pending";
    }

    @Override
    public void onRequestLocationSent(String friendName) {
        locationSentMessage = "We sent your location request to "+friendName;
    }

    @Override
    public void onActionDeleteLocation(final String refId, final String userId) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        /**Implement Fragment Method**/
                        final Firebase ref = new Firebase(Constants.BASE_URL);
                        final Firebase myLocationRef = ref.child("users").child(userId).child("myLocations").child(refId);
                        myLocationRef.setValue(null);
                        getFragmentManager().popBackStack();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainPrivateActivity.this);
        builder.setTitle("Confirm").setMessage("Are you sure you want to delete this location?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public String sendMessagetoShowPopUp(){
        if(locationSentMessage.equalsIgnoreCase("")){
            return "";
        }
        else{
            String message = locationSentMessage;
            locationSentMessage = "";
            return message;
        }
    }


    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                if(isFragmentInBackstack(fragmentManager,"profile")){
                    fragmentManager.popBackStackImmediate("profile", 0);
                }
                else{
                    fragment = new ProfileFragment();
                    fragmentTransaction.replace(R.id.frame_container, fragment, "profile");
                    fragmentTransaction.addToBackStack("profile");
                    fragmentTransaction.commit();
                }
                break;
            case 1:
                fragment = new HomePrivateFragment();
                fragmentTransaction.replace(R.id.frame_container, fragment, "home");
                fragmentTransaction.commit();
                break;
            case 2:
                if(isFragmentInBackstack(fragmentManager,"myLocation")){
                    fragmentManager.popBackStackImmediate("myLocation", 0);
                }
                else{
                    fragment = new MyLocationsFragment();
                    fragmentTransaction.replace(R.id.frame_container, fragment, "myLocation");
                    fragmentTransaction.addToBackStack("myLocation");
                    fragmentTransaction.commit();
                }
                break;
            case 3:
                fragment = new ContactsFragment();
                Bundle bndle = new Bundle();
                bndle.putString("action", "chat");
                fragment.setArguments(bndle);
                fragmentTransaction.replace(R.id.frame_container, fragment, "chat");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 4:
                fragment = new NavigationHistoryListFragment();
                fragmentTransaction.replace(R.id.frame_container, fragment, "navHistoryList");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 5:
                fragment = new SettingsFragment();
                fragmentTransaction.replace(R.id.frame_container, fragment, "settings");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 6:
                fragment = new AboutFragment();
                fragmentTransaction.replace(R.id.frame_container, fragment, "about");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            default:
                break;
        }

        if (fragment != null) {
            mDrawerList.setItemChecked(position, false);
            mDrawerList.setSelection(position);
            if(position>0) {
                if (position == 3) {
                    setTitle("Chats");
                }
                else{
                    setTitle(navMenuTitles[position - 1]);
                }

            }
            else{
                setTitle("PROFILE");
            }
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            mDrawerList.setItemChecked(position, false);
            mDrawerLayout.closeDrawer(mDrawerList);
            Log.e("MainActivity", "Fragment is already in backstack");
        }
    }

    public static boolean isFragmentInBackstack(final FragmentManager fragmentManager, final String fragmentTagName) {
        for (int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++) {
            if (fragmentTagName.equals(fragmentManager.getBackStackEntryAt(entry).getName())) {
                return true;
            }
        }
        return false;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_private, menu);
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("", "resume is called");

        final Firebase ref = new Firebase(Constants.BASE_URL);
        final Firebase myStatusRef = ref.child("users").child(userId).child("blocked");
        final AlertDialog blockMessage = new AlertDialog.Builder(MainPrivateActivity.this)
                .setTitle("Error!")
                .setMessage("Your account is blocked. Retry when an admin has unblocked it.")
                .setCancelable(false)
                .create();
        myStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null &&
                        dataSnapshot.getValue().equals(true)) {
                    Toast.makeText(MainPrivateActivity.this, "User is blocked", Toast.LENGTH_SHORT).show();
                    blockMessage.show();
                } else {
                    if (blockMessage.isShowing()) {
                        blockMessage.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        final NavigationFragment myFragment = (NavigationFragment)getFragmentManager().findFragmentByTag("navFragment");
        final AddEditLocationFragment locFragment = (AddEditLocationFragment)getFragmentManager().findFragmentByTag("editLocation");
        final ProfileFragment profileFragment = (ProfileFragment)getFragmentManager().findFragmentByTag("profile");
        final MyLocationsFragment myLocationsFragment = (MyLocationsFragment)getFragmentManager().findFragmentByTag("myLocation");
        final ContactsFragment chatFragment = (ContactsFragment)getFragmentManager().findFragmentByTag("chat");
        final NavigationHistoryListFragment navHistoryFragment = (NavigationHistoryListFragment)getFragmentManager().findFragmentByTag("navHistoryList");
        final SettingsFragment settingsFragment = (SettingsFragment)getFragmentManager().findFragmentByTag("settings");
        final AboutFragment aboutFragment = (AboutFragment)getFragmentManager().findFragmentByTag("about");
        if (myFragment!=null && myFragment.isVisible()) {
            // add your code here
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            /**Implement Fragment Method**/
                            myFragment.closeNavigation();
                            getFragmentManager().popBackStack();

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MainPrivateActivity.this);
            builder.setTitle("Confirm").setMessage("Are you sure? Current Navigation will be stopped").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
       else if (locFragment!=null && locFragment.isVisible()) {
            // add your code here
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            /**Implement Fragment Method**/
                            getFragmentManager().popBackStack();

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MainPrivateActivity.this);
            builder.setTitle("Warning").setMessage("Are you sure you want to go back? All unsaved changes to location will be removed.").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
        else if(profileFragment!=null&&profileFragment.isVisible()){
            clearBackStack();
        }
        else if(myLocationsFragment!=null&&myLocationsFragment.isVisible()){
            clearBackStack();
        }
        else if(chatFragment!=null&&chatFragment.isVisible()){
            clearBackStack();
        }
        else if(navHistoryFragment!=null&&navHistoryFragment.isVisible()){
            clearBackStack();
        }
        else if(aboutFragment!=null&&aboutFragment.isVisible()){
            clearBackStack();
        }
        else if(settingsFragment!=null&&settingsFragment.isVisible()){
            clearBackStack();
        }
        else
        {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                /**Implement Fragment Method**/
                                dialog.dismiss();
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPrivateActivity.this);
                builder.setTitle("Warning").setMessage("Are you sure you want to exit FetchMeThere?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
//                finish();
            }
        }


    }

    private void clearBackStack() {
        final FragmentManager fragmentManager = getFragmentManager();
        while (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStackImmediate();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.
//                findItem(R.id.action_settings).
//                setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
