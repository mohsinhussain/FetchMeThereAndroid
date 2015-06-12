package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capricorn.ArcMenu;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.webmedia7.mohsinhussain.fetchmethere.Activities.MainPrivateActivity;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.RoundedImageView;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class HomePrivateFragment extends Fragment{

    public HomePrivateFragment(){}

    private OnFragmentInteractionListener mListener;

    MapView mapView;
    GoogleMap map;
    Button myLocationsButton;
    ImageView arcMenuBgLayout;
    private static final int[] ITEM_DRAWABLES = { R.drawable.request_location_button_bg, R.drawable.send_location_button_bg,
            R.drawable.business_button_bg};
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 2;
    String userId = "";
    String myMobileNumber = "";
    String myDisplayName = "";
    String myProfileImageString = "";
    SharedPreferences preferenceSettings;
    LinearLayout notificationLayout, messageLayout, questionLayout, acceptenceLayout;
    TextView messageTextView, questionTextView;
    Button yesButton, noButton;
    double currentLat = 0;
    double currentLang = 0;
    Marker currentMarker;
    ProgressDialog ringProgressDialog;
    RoundedImageView profileImageView;
    Timer timer;
    boolean gps_enabled = false;
    boolean network_enabled = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home_private, container, false);

        preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
        userId = preferenceSettings.getString("userId", null);
        myMobileNumber = preferenceSettings.getString("mobileNumber", null);
        myDisplayName = preferenceSettings.getString("displayName", null);
        myProfileImageString = preferenceSettings.getString("profileImageString", null);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) rootView.findViewById(R.id.mapview);
        myLocationsButton = (Button) rootView.findViewById(R.id.locationsButton);
        arcMenuBgLayout = (ImageView) rootView.findViewById(R.id.arc_menu_bg);
        notificationLayout = (LinearLayout) rootView.findViewById(R.id.notificationLayout);
        messageLayout = (LinearLayout) rootView.findViewById(R.id.messageLayout);
        questionLayout = (LinearLayout) rootView.findViewById(R.id.questionLayout);
        acceptenceLayout = (LinearLayout) rootView.findViewById(R.id.acceptenceLayout);

        messageTextView = (TextView) rootView.findViewById(R.id.messageTextView);
        questionTextView = (TextView) rootView.findViewById(R.id.questionTextView);
        yesButton = (Button) rootView.findViewById(R.id.yesButton);
        noButton = (Button) rootView.findViewById(R.id.noButton);
        profileImageView = (RoundedImageView) rootView.findViewById(R.id.profile_image_view);


        myLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMyLocationsClicked();
                }
            }
        });
        //Arc Menu Implementation
        final ArcMenu menu = (ArcMenu) rootView.findViewById(R.id.arc_menu);

        menu.controlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(getTag(), "control layout clicked");
                if(menu.mArcLayout.isExpanded()){
                    arcMenuBgLayout.setVisibility(View.VISIBLE);
                }
                else{
                    arcMenuBgLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
//        menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.v(getTag(), "menu clicked");
//            }
//        });


        final int itemCount = 3;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(getActivity());
            item.setImageResource(ITEM_DRAWABLES[i]);

            final int position = i;

//            ExampleLayout exampleLayout = new ExampleLayout(getActivity(), item, "a");
            menu.addItem(item, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            mListener.onRequestLocation();
                            arcMenuBgLayout.setVisibility(View.INVISIBLE);
                            break;
                        case 1:
                            mListener.onSendLocationClicked(currentLat, currentLang);
                            arcMenuBgLayout.setVisibility(View.INVISIBLE);
                            break;

                        case 3:
                            mListener.onBusinessesClicked();
                            arcMenuBgLayout.setVisibility(View.INVISIBLE);
                            break;


                        default:
                            break;
                    }
//                    Toast.makeText(getActivity(), "position:" + position, Toast.LENGTH_SHORT).show();
                }
            });// Add a menu item
        }


        initMap(savedInstanceState);




        return rootView;
    }

    @Override
    public void onStop() {
        locationManager.removeUpdates(locationListenerGps);
        super.onStop();
    }

    @Override
    public void onPause() {
        locationManager.removeUpdates(locationListenerGps);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        locationManager.removeUpdates(locationListenerGps);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        getActivity().setTitle("HOME");
        mapView.onResume();
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListenerGps); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
        updateLocation();

        //Show Location Sent notfication
        MainPrivateActivity activity = (MainPrivateActivity) getActivity();
        String messageString = activity.sendMessagetoShowPopUp();

        if (!messageString.equalsIgnoreCase("")){
            messageTextView.setText(messageString);
            showPopOver();
        }

        //Show location received pop up
        final Firebase ref = new Firebase(Constants.BASE_URL);
        final Firebase locationReceivedRef = ref.child("users").child(userId).child("receivedLocations");

        locationReceivedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for (final DataSnapshot child : snapshot.getChildren()) {
                        String friendId = "";
                        String friendName = "";
                        String locName = "";
                        String address = "";
                        String lat = "";
                        String lang = "";
                        String comment = "";
                        String profileImageString = "";
                        for (DataSnapshot mChild : child.getChildren()) {
                            if (mChild.getKey().equalsIgnoreCase("friendId")) {
                                friendId = mChild.getValue().toString();
                            } else if (mChild.getKey().equalsIgnoreCase("friendName")) {
                                friendName = mChild.getValue().toString();
                            }
                            else if (mChild.getKey().equalsIgnoreCase("locName")) {
                                locName = mChild.getValue().toString();
                            }
                            else if (mChild.getKey().equalsIgnoreCase("address")) {
                                address = mChild.getValue().toString();
                            }
                            else if (mChild.getKey().equalsIgnoreCase("lat")) {
                                lat = mChild.getValue().toString();
                            }
                            else if (mChild.getKey().equalsIgnoreCase("lang")) {
                                lang = mChild.getValue().toString();
                            }
                            else if (mChild.getKey().equalsIgnoreCase("profileImageString")) {
                                profileImageString = mChild.getValue().toString();
                            }
                            else if (mChild.getKey().equalsIgnoreCase("comment")) {
                                comment = mChild.getValue().toString();
                                if(comment.equalsIgnoreCase("")){
                                    comment = "None";
                                }
                            }
                        }
                        Constants.setImageViewFromString(profileImageString, profileImageView);
                        messageTextView.setText(friendName + " wants to send location. Comments: " + comment);
                        questionTextView.setText("Do you want us to Fetch you there?");
                        yesButton.setText("FETCH ME THERE");
                        noButton.setText("DECLINE");
                        final String finalFriendId = friendId;
                        final String finalFriendName = friendName;
                        final String finalLocName = locName;
                        final String finalLat = lat;
                        final String finalLang = lang;
                        final String finalAddress = address;
                        final String finalProfileImageString = profileImageString;
                        yesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hideNotification();
                                /**Add navigation detail to NAVIGATION HISTORY
                                 *
                                 * Notify sender of location received and accepted
                                 *
                                 * Start Navigation
                                 *
                                 *
                                 *
                                 * **/


                                if(mListener!=null){
                                    mListener.onStartNavigation(finalFriendId, finalFriendName, finalLocName, finalLat, finalLang, finalAddress, currentLat, currentLang, finalProfileImageString);
                                }


//                                locationReceivedRef.setValue(null);
//                                Toast.makeText(getActivity().getApplicationContext(), "Fetch Him there!", Toast.LENGTH_LONG).show();

                            }
                        });

                        noButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hideNotification();
                                locationReceivedRef.setValue(null);

//                                Toast.makeText(getActivity().getApplicationContext(), "Ignore and store it to navigation history", Toast.LENGTH_LONG).show();
                            }
                        });
                        showNotification();
                    }

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });



        //Show location requested pop up
        final Firebase locationRequestedRef = ref.child("users").child(userId).child("locationRequests");

        locationRequestedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for (final DataSnapshot child : snapshot.getChildren()) {
                        String friendId = "";
                        String friendName = "";
                        String comment = "";
                        String profileImageString = "";
                        for (DataSnapshot mChild : child.getChildren()) {
                            if (mChild.getKey().equalsIgnoreCase("friendId")) {
                                friendId = mChild.getValue().toString();
                            } else if (mChild.getKey().equalsIgnoreCase("friendName")) {
                                friendName = mChild.getValue().toString();
                            }
                            else if (mChild.getKey().equalsIgnoreCase("profileImageString")) {
                                profileImageString = mChild.getValue().toString();
                            }
                            else if (mChild.getKey().equalsIgnoreCase("comment")) {
                                comment = mChild.getValue().toString();
                                if(comment.equalsIgnoreCase("")){
                                    comment = "None";
                                }
                            }
                        }

                        Constants.setImageViewFromString(profileImageString, profileImageView);
                        messageTextView.setText(friendName + " just requested your current location.\nComments: "+comment);
                        questionTextView.setText("Do you want us to send your current location?");
                        yesButton.setText("SEND LOCATION");
                        noButton.setText("DECLINE");
                        final String finalFriendId = friendId;
                        yesButton.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                             hideNotification();
                                                             Geocoder geocoder;
                                                             String snippet = "Unknown";

                                                             List<Address> addresses;
                                                             geocoder = new Geocoder(getActivity(), Locale.getDefault());

                                                             try {
                                                                 addresses = geocoder.getFromLocation(currentLat, currentLang, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                                                 snippet = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName(); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                                             } catch (IOException e) {
                                                                 e.printStackTrace();
                                                             }
                                                             final String finalSnippet = snippet;
                                                             ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Sending Location...", true);
                                                             ringProgressDialog.setCancelable(true);

                                                             final Firebase ref = new Firebase(Constants.BASE_URL);
                                                             Firebase postRef = ref.child("users").child(finalFriendId).child("receivedLocations");

                                                             Map<String, String> post1 = new HashMap<String, String>();
                                                             post1.put("friendId", userId);
                                                             post1.put("friendName", myDisplayName);
                                                             post1.put("locName", "Current Location");
                                                             post1.put("address", finalSnippet);
                                                             post1.put("lat", Double.toString(currentLat));
                                                             post1.put("lang", Double.toString(currentLang));
                                                             post1.put("profileImageView", myProfileImageString);
                                                             post1.put("comment", "Here is my current location");

                                                             postRef.push().setValue(post1, new Firebase.CompletionListener() {
                                                                 @Override
                                                                 public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                                     ringProgressDialog.dismiss();
                                                                     if (firebaseError != null) {
                                                                         Toast.makeText(getActivity().getApplicationContext(), "Location could not be sent.", Toast.LENGTH_LONG).show();
                                                                     } else {

                                                                         System.out.println("Data saved successfully.");
                                                                         Toast.makeText(getActivity().getApplicationContext(), "Your current location is sent successfully", Toast.LENGTH_LONG).show();
                                                                     }
                                                                 }
                                                             });
                                                             locationRequestedRef.setValue(null);

                                                         }
                                                     });


                        noButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hideNotification();
                                locationRequestedRef.setValue(null);

//                                Toast.makeText(getActivity().getApplicationContext(), "Ignore and store it to navigation history", Toast.LENGTH_LONG).show();
                            }
                        });
                        showNotification();
                    }

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });




        //Show pending friend request pop up
        final Firebase postRef = ref.child("users").child(userId).child("pendingFriends");

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for (final DataSnapshot child : snapshot.getChildren()) {
                        String name = "";
                        String mobileNumber = "";
                        String friendProfileImageString = "";
                        for(DataSnapshot mChild : child.getChildren()){
                            if(mChild.getKey().equalsIgnoreCase("name")){
                                name = mChild.getValue().toString();
                            }
                            else if(mChild.getKey().equalsIgnoreCase("mobileNumber")){
                                mobileNumber = mChild.getValue().toString();
                            }
                            else if(mChild.getKey().equalsIgnoreCase("profileImageString")){
                                friendProfileImageString = mChild.getValue().toString();
                            }
                        }
                        Constants.setImageViewFromString(friendProfileImageString, profileImageView);
                        messageTextView.setText(name + " wants to add you as his friend. You will be able to share your location with him after friendship.");
                        questionTextView.setText("Do you want to Add "+name+" as your friend? Mobile Number: "+mobileNumber);
                        final String finalMobileNumber = mobileNumber;
                        final String finalName = name;
                        final String finalFriendProfileImageString = friendProfileImageString;
                        yesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hideNotification();
                                final Firebase addFriendRef = ref.child("users").child(userId).child("friends");
                                Map<String, Object> post1 = new HashMap<String, Object>();
                                post1.put("userId", child.getKey());
                                post1.put("mobile", finalMobileNumber);
                                post1.put("name", finalName);
                                post1.put("profileImageString", finalFriendProfileImageString);
                                addFriendRef.push().setValue(post1);

                                final Firebase addFriendtoMeRef = ref.child("users").child(child.getKey()).child("friends");
                                Map<String, Object> post2 = new HashMap<String, Object>();
                                post2.put("userId", userId);
                                post2.put("mobile", myMobileNumber);
                                post2.put("name", myDisplayName);
                                post2.put("profileImageString", myProfileImageString);
                                addFriendtoMeRef.push().setValue(post2);

                                postRef.setValue(null);

                                Toast.makeText(getActivity().getApplicationContext(), "Friend Added Successfully", Toast.LENGTH_LONG).show();

                            }
                        });

                        noButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hideNotification();
                                postRef.setValue(null);

                                Toast.makeText(getActivity().getApplicationContext(), "Friend Request Ignored", Toast.LENGTH_LONG).show();
                            }
                        });
                        showNotification();
                    }

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mapView!=null){
            mapView.onDestroy();
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void hideNotification(){

        TranslateAnimation anim = new TranslateAnimation( 0, 0,0, -400);
        anim.setDuration(1500);
        anim.setFillAfter(true);
        notificationLayout.startAnimation(anim);

        notificationLayout.setVisibility(View.GONE);
    }

    public void showNotification(){
        notificationLayout.setVisibility(View.VISIBLE);
        questionLayout.setVisibility(View.VISIBLE);
        acceptenceLayout.setVisibility(View.VISIBLE);

        TranslateAnimation anim = new TranslateAnimation( 0, 0, -400, 0 );
        anim.setDuration(1500);
        anim.setInterpolator(new BounceInterpolator());
        anim.setFillAfter(true);
        notificationLayout.startAnimation(anim);
    }

    public void showPopOver(){
        notificationLayout.setVisibility(View.VISIBLE);
        profileImageView.setVisibility(View.GONE);
        questionLayout.setVisibility(View.GONE);
        acceptenceLayout.setVisibility(View.GONE);
        TranslateAnimation anim = new TranslateAnimation( 0, 0, -400, notificationLayout.getHeight() );
        anim.setDuration(1500);
        anim.setInterpolator(new BounceInterpolator());
        anim.setFillAfter(true);
        notificationLayout.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                keepPopOver();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void keepPopOver(){
        TranslateAnimation anim = new TranslateAnimation( 0, 0,0, 0);
        anim.setDuration(4000);
        anim.setFillAfter(true);
        notificationLayout.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hidePopOver();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void hidePopOver() {

        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -400);
        anim.setDuration(1500);
        anim.setInterpolator(new BounceInterpolator());
        anim.setFillAfter(true);
        notificationLayout.startAnimation(anim);
    }



    public void initMap(Bundle savedInstanceState){
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

//        Log.v("PROVIDER", "GPS: "+LocationManager.GPS_PROVIDER);
//        Log.v("PROVIDER", "Passive: "+LocationManager.PASSIVE_PROVIDER);
//        Log.v("PROVIDER", "Network: "+LocationManager.NETWORK_PROVIDER);
//        Criteria criteria=new Criteria();
//        String provider=locationManager.getBestProvider(criteria, false);
//        Log.v("PROVIDER", "BEST: "+provider);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

    }

    public void updateLocation(){
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!gps_enabled){
            if(canToggleGPS()){
                turnGPSOn();
            }
            else{
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }



        if (!gps_enabled && !network_enabled) {  Context context = getActivity();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "no provider is enabled", duration);
            toast.show();
            Location location = locationManager
                    .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (location != null) {
                currentLat = location.getLatitude();
                currentLang = location.getLongitude();
            }
        }

        if (gps_enabled)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    locationListenerGps);
        if (network_enabled)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListenerNetwork);
        timer=new Timer();
        timer.schedule(new GetLastLocation(), 20000);


        LatLng latLng = new LatLng(currentLat, currentLang);

        // Showing the current location in Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Zoom in the Google Map
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

        Geocoder geocoder;
        String snippet = "Unknown";

        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(currentLat, currentLang, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if(addresses.size()>0){
                snippet = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getCountryName(); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            }

//            currentAddress = snippet;
        } catch (IOException e) {
            e.printStackTrace();
        }


        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_bg);


        MarkerOptions a = new MarkerOptions()
                .title("You are here")
                .position(latLng)
                .snippet(snippet)
                .icon(icon);

        currentMarker = map.addMarker(a);
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            currentLat =location.getLatitude();
            currentLang = location.getLongitude();
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);
            setNewPosition();
            Context context = getActivity();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "gps enabled "+currentLat + "\n" + currentLang, duration);
            toast.show();

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            currentLat = location.getLatitude();
            currentLang = location.getLongitude();
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGps);
            setNewPosition();
            Context context = getActivity();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "network enabled"+currentLat + "\n" + currentLang, duration);
            toast.show();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private boolean canToggleGPS() {
        PackageManager pacman = getActivity().getPackageManager();
        PackageInfo pacInfo = null;

        try {
            pacInfo = pacman.getPackageInfo("com.android.settings", PackageManager.GET_RECEIVERS);
        } catch (PackageManager.NameNotFoundException e) {
            return false; //package not found
        }

        if (pacInfo != null) {
            for (ActivityInfo actInfo : pacInfo.receivers){
                //test if recevier is exported. if so, we can toggle GPS.
                if (actInfo.name.equals("com.android.settings.widget.SettingsAppWidgetProvider") && actInfo.exported){
                    return true;
                }
            }
        }

        return false; //default
    }

    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            getActivity().sendBroadcast(poke);
        }
    }

    public void setNewPosition(){
        LatLng latLng = new LatLng(currentLat, currentLang);
        Log.v("HomePrivateFragment", "Lat:"+currentLat+" Lat:"+currentLang);
        // Showing the current location in Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Zoom in the Google Map
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

        Geocoder geocoder;
        String snippet = "Unknown";

        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(currentLat, currentLang, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if(addresses.size()>0){
                snippet = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getCountryName(); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            }

//            currentAddress = snippet;
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentMarker.setPosition(latLng);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            locationManager.removeUpdates(locationListenerGps);
            locationManager.removeUpdates(locationListenerNetwork);

            Location net_loc = null, gps_loc = null;
            if (gps_enabled)
                gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled)
                net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            //if there are both values use the latest one
            if (gps_loc != null && net_loc != null) {
                if (gps_loc.getTime() > net_loc.getTime()) {
                    currentLat = gps_loc.getLatitude();
                    currentLang = gps_loc.getLongitude();
                    Context context = getActivity();
                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "network lastknown " + currentLat + "\n" + currentLang, Toast.LENGTH_SHORT).show();
                        }
                    }; // This is your code
                    mainHandler.post(myRunnable);
                } else {
                    currentLat = net_loc.getLatitude();
                    currentLang = net_loc.getLongitude();
                    Context context = getActivity();
                    // Get a handler that can be used to post to the main thread
                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "network lastknown " + currentLat + "\n" + currentLang, Toast.LENGTH_SHORT).show();
                        }
                    }; // This is your code
                    mainHandler.post(myRunnable);
                }

            }

            if (gps_loc != null) {
                {
                    currentLat = gps_loc.getLatitude();
                    currentLang = gps_loc.getLongitude();
                    Context context = getActivity();
                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "network lastknown " + currentLat + "\n" + currentLang, Toast.LENGTH_SHORT).show();
                        }
                    }; // This is your code
                    mainHandler.post(myRunnable);
                }

            }
            if (net_loc != null) {
                {
                    currentLat = net_loc.getLatitude();
                    currentLang = net_loc.getLongitude();
                    Context context = getActivity();
                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "network lastknown " + currentLat + "\n" + currentLang, Toast.LENGTH_SHORT).show();
                        }
                    }; // This is your code
                    mainHandler.post(myRunnable);

                }
            }
            Context context = getActivity();
            Handler mainHandler = new Handler(context.getMainLooper());
            Runnable myRunnable = new Runnable(){
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "network lastknown " + currentLat + "\n" + currentLang, Toast.LENGTH_SHORT).show();
                    Location location = locationManager
                            .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLang = location.getLongitude();
                        Log.v("HomePrivateFragment", "no last known available");
                    }
                }
            }; // This is your code
            mainHandler.post(myRunnable);

//            setNewPosition();
        }
    }






//    @Override
//    public void onLocationChanged(Location location) {
//
//        // Getting latitude of the current location
//        double latitude = location.getLatitude();
//        currentLat = latitude;
//
//        // Getting longitude of the current location
//        double longitude = location.getLongitude();
//        currentLang = longitude;
//
//        // Creating a LatLng object for the current location
//        LatLng latLng = new LatLng(currentLat, currentLang);
//        currentMarker.setPosition(latLng);
//
//
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }

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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onMyLocationsClicked();
        public void onRequestLocation();
        public void onSendLocationClicked(double lat, double lang);
        public void onBusinessesClicked();
        public void onStartNavigation(String friendId, String friendName, String locationName, String lat, String lang, String address, double currentLat, double currentLang, String profileImageString);
    }
}
