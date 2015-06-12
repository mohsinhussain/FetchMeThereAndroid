package com.webmedia7.mohsinhussain.fetchmethere.Activities;

import android.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
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
    private String profileImageString;
    String locationSentMessage = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main_private);
        Firebase.setAndroidContext(this);

        preferenceSettings = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        displayName = preferenceSettings.getString("displayName", null);
        profileImageString = preferenceSettings.getString("profileImageString", null);
        Log.v("HELOO",displayName);


        mTitle = getTitle();
        mDrawerTitle = "DrawerTitle";

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
        bndle.putString("profileImageString", profileImageString);
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
        fragmentTransaction.replace(R.id.frame_container, editLocationsFragment);
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


//        FragmentManager fm = getFragmentManager();
//        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
//            fm.popBackStack();
//        }
//        Fragment homeFragment = new HomePrivateFragment();
//        Bundle bndle = new Bundle();
//        bndle.putString("action", "locationSent");
//        bndle.putString("friendName", friendName);
//        bndle.putString("locationName", locationName);
//        homeFragment.setArguments(bndle);
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_container, homeFragment);
//        fragmentTransaction.commit();
    }

    @Override
    public void onRequestLocationSent(String friendName) {
        locationSentMessage = "We sent your location request to "+friendName;
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
        switch (position) {
            case 0:
                fragment = new ProfileFragment();
                break;
            case 1:
                fragment = new HomePrivateFragment();
                break;
            case 2:
                fragment = new MyLocationsFragment();
                break;
//            case 3:
//                fragment = new FavouriteBusinessFragment();
//                break;
            case 3:
                fragment = new ContactsFragment();
                Bundle bndle = new Bundle();
                bndle.putString("action", "chat");
                fragment.setArguments(bndle);
                break;
            case 4:
                fragment = new NavigationHistoryListFragment();
                break;
            case 5:
                fragment = new SettingsFragment();
                break;
            case 6:
                fragment = new AboutFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();

//            FragmentManager fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            if(position>0){
                setTitle(navMenuTitles[position-1]);
            }
            else{
                setTitle("PROFILE");
            }
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            mDrawerList.setItemChecked(position, false);
            Log.e("MainActivity", "Profile Clicked");
        }
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
    }

    @Override
    public void onBackPressed() {

        final NavigationFragment myFragment = (NavigationFragment)getFragmentManager().findFragmentByTag("navFragment");
        if (myFragment!=null && myFragment.isVisible()) {
            // add your code here
            Toast.makeText(getApplicationContext(), "show pop over", Toast.LENGTH_LONG).show();
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
        else
        {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                finish();
            }
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
