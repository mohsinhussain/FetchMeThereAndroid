package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.DirectionsJSONParser;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Speaker;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class NavigationFragment extends Fragment implements LocationListener {

    public NavigationFragment(){}
    MapView mapView;
    GoogleMap map;
    private double lat;
    private double lang;
    String userId = "";
    String myDisplayName = "";
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1;
    SharedPreferences preferenceSettings;
    LinearLayout mainLinearLayout;
    private LocationManager locationManager;
    double currentLat = 0;
    double currentLang = 0;
    String currentAddress = "";
    String friendId;
    String friendName;
    String friendLat;
    String friendLang;
    String friendLocName;
    String friendAddress;
    String myNavHistReference, friendHistReference;
    LatLng destinationLatLang;
    LatLng originLatLang;
    TextView tvDistanceDuration, instrunctionsTextView;
    ArrayList<LatLng> endPoints = new ArrayList<LatLng>();
    ArrayList<String> maneuvers = new ArrayList<String>();
    ArrayList<String> html = new ArrayList<String>();
    ProgressDialog ringProgressDialog;
    String totalDuration = "";
    String totalDistance = "";
    int endPointPosition = 0;
//    TextToSpeech ttobj;
    Marker currentMarker;
    Polyline mPolyLine;
    int nextPointDistance = 0;
    int stray = 0;
    ImageView bikeButton, walkButton, carButton;
    String mode;
    private Menu mMenu;
    String myProfileImageString = "";
    String friendProfileImageString = "";
    ImageView maneuverImageView;

    private final int CHECK_CODE = 0x1;
    private final int LONG_DURATION = 5000;
    private final int SHORT_DURATION = 1200;

    private Speaker speaker;

//    private OnFragmentInteractionListener mListener;

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHECK_CODE){
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                speaker = new Speaker(getActivity());
                speaker.allow(true);
            }else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_navigation, container, false);
        mapView = (MapView) rootView.findViewById(R.id.mapview);

        this.tvDistanceDuration = (TextView) rootView.findViewById(R.id.instructions_text_view);
        this.instrunctionsTextView = (TextView) rootView.findViewById(R.id.tv_distance_time);

        bikeButton = (ImageView) rootView.findViewById(R.id.cycleButton);
        walkButton = (ImageView) rootView.findViewById(R.id.walkButton);
        carButton = (ImageView) rootView.findViewById(R.id.carButton);
        maneuverImageView = (ImageView) rootView.findViewById(R.id.maneuverImageView);

        bikeButton.setVisibility(View.GONE);
        walkButton.setVisibility(View.GONE);
        carButton.setVisibility(View.GONE);

        mode = "mode=driving";




        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = "mode=driving";
                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(originLatLang, destinationLatLang, mode);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

                carButton.setVisibility(View.GONE);
                walkButton.setVisibility(View.GONE);
                bikeButton.setVisibility(View.GONE);

                mMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.car));

            }
        });

        bikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = "mode=bicycling";
                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(originLatLang, destinationLatLang, mode);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

                carButton.setVisibility(View.GONE);
                walkButton.setVisibility(View.GONE);
                bikeButton.setVisibility(View.GONE);

                mMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.bike));

            }
        });

        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = "mode=walking";
                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(originLatLang, destinationLatLang, mode);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

                carButton.setVisibility(View.GONE);
                walkButton.setVisibility(View.GONE);
                bikeButton.setVisibility(View.GONE);

                mMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.walk));

            }
        });

        checkTTS();

        // Initializing


//        ttobj=new TextToSpeech(getActivity(),
//                new TextToSpeech.OnInitListener() {
//                    @Override
//                    public void onInit(int status) {
//                        if(status != TextToSpeech.ERROR){
//                            ttobj.setLanguage(Locale.UK);
//                        }
//                    }
//                });

        preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
        userId = preferenceSettings.getString("userId", null);
        myDisplayName = preferenceSettings.getString("displayName", null);
        myProfileImageString = preferenceSettings.getString("profileImageString", null);

        setHasOptionsMenu(true);

        initMap(savedInstanceState);




        Bundle bundle = this.getArguments();

        if(bundle.containsKey("action")) {
            if (bundle.getString("action", null).equalsIgnoreCase("object")) {
                friendId = bundle.getString("friendId");
                friendName = bundle.getString("friendName");
                friendLat = bundle.getString("lat");
                friendLang = bundle.getString("lang");
                friendLocName = bundle.getString("locationName");
                friendAddress = bundle.getString("address");
                friendProfileImageString = bundle.getString("profileImageString");
                currentLat = bundle.getDouble("currentLat");
                currentLang = bundle.getDouble("currentLang");
                String navigationMode = "";
                if(mode.contains("driving")){
                    navigationMode = "driving";
                }
                else if(mode.contains("walking")){
                    navigationMode = "walking";
                }
                else if(mode.contains("bicycling")){
                    navigationMode = "bicycling";
                }
                ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Initializing Fetch Me There...", true);
                ringProgressDialog.setCancelable(true);

                final Firebase ref = new Firebase(Constants.BASE_URL);
                Firebase postRef = ref.child("users").child(userId).child("navigationHistory");

                Map<String, String> post1 = new HashMap<String, String>();
                post1.put("friendId", friendId);
                post1.put("friendName", friendName);
                post1.put("friendLat", friendLat);
                post1.put("friendLang", friendLang);
                post1.put("friendLocName", friendLocName);
                post1.put("friendAddress", friendAddress);
                post1.put("profileImageString", friendProfileImageString);
                post1.put("myUserId", userId);
                post1.put("myDisplayName", myDisplayName);
                post1.put("myLat", Double.toString(currentLat));
                post1.put("myLang", Double.toString(currentLang));
                post1.put("myAddress", currentAddress);
                post1.put("navStartTime", Long.toString(System.currentTimeMillis()));
                post1.put("distanceCovered", "0");
                post1.put("navEndTime", Long.toString(System.currentTimeMillis()));
                post1.put("totalDistance", totalDistance);
                post1.put("totalDuration", totalDuration);
                post1.put("mode", navigationMode);
                post1.put("status", "active");
                post1.put("action", "object");

                Firebase pushRef = postRef.push();

                pushRef.setValue(post1, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(getActivity().getApplicationContext(), "Initialization Failed." + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            System.out.println("Data saved successfully.");
                        }
                    }
                });

                myNavHistReference = pushRef.getKey();


                Firebase postFriendRef = ref.child("users").child(friendId).child("navigationHistory");

                Map<String, String> post2 = new HashMap<String, String>();
                post2.put("friendId", userId);
                post2.put("friendName", myDisplayName);
                post2.put("friendLat", Double.toString(currentLat));
                post2.put("friendLang", Double.toString(currentLang));
                post2.put("friendLocName", "Current Location");
                post2.put("friendAddress", currentAddress);
                post1.put("profileImageString", myProfileImageString);
                post2.put("myUserId", friendId);
                post2.put("myDisplayName", friendName);
                post2.put("myLat", friendLat);
                post2.put("myLang", friendLang);
                post2.put("myAddress", friendAddress);
                post2.put("navStartTime", Long.toString(System.currentTimeMillis()));
                post2.put("distanceCovered", "0");
                post1.put("navEndTime", Long.toString(System.currentTimeMillis()));
                post1.put("totalDistance", totalDistance);
                post1.put("totalDuration", totalDuration);
                post2.put("status", "active");
                post2.put("action", "destination");

                Firebase pushFriendRef = postFriendRef.push();

                pushFriendRef.setValue(post2, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
//                        ringProgressDialog.dismiss();
                        if (firebaseError != null) {
                            Toast.makeText(getActivity().getApplicationContext(), "Initialization Failed." + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            System.out.println("Data saved successfully.");
                            Firebase postActiveNav = ref.child("users").child(userId).child("activeNavigation");

                            Map<String, String> post1 = new HashMap<String, String>();
                            post1.put("friendId", friendId);
                            post1.put("friendName", friendName);
                            post1.put("friendLat", friendLat);
                            post1.put("friendLang", friendLang);
                            post1.put("friendLocName", friendLocName);
                            post1.put("friendAddress", friendAddress);
                            post1.put("profileImageString", friendProfileImageString);
                            post1.put("myUserId", userId);
                            post1.put("myDisplayName", myDisplayName);
                            post1.put("myLat", Double.toString(currentLat));
                            post1.put("myLang", Double.toString(currentLang));
                            post1.put("myAddress", currentAddress);
                            post1.put("navStartTime", Long.toString(System.currentTimeMillis()));
                            post1.put("distanceCovered", "0");
                            post1.put("status", "active");
                            post1.put("action", "object");

//                            Firebase pushFriendRef = postFriendRef.push();

                            postActiveNav.setValue(post1, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    ringProgressDialog.dismiss();
                                    if (firebaseError != null) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Initialization Failed." + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    } else {
                                        System.out.println("Data saved successfully.");

                                    }
                                }
                            });
                        }
                    }
                });

                friendHistReference = pushFriendRef.getKey();



            }
        }


        destinationLatLang = new LatLng(Double.parseDouble(friendLat), Double.parseDouble(friendLang));
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_bg);
        MarkerOptions markerOptions = new MarkerOptions().position(destinationLatLang)
                .title(friendName + " is here")
                .position(destinationLatLang)
                .snippet(friendAddress)
                .icon(icon);

        map.addMarker(markerOptions);


        bundle = this.getArguments();
        if(bundle.containsKey("action")) {
            if (bundle.getString("action", null).equalsIgnoreCase("object")) {
                friendId = bundle.getString("friendId");
                friendName = bundle.getString("friendName");
                friendLat = bundle.getString("lat");
                friendLang = bundle.getString("lang");
                friendLocName = bundle.getString("locationName");
                friendAddress = bundle.getString("address");
                currentLat = bundle.getDouble("currentLat");
                currentLang = bundle.getDouble("currentLang");
            }
        }

//        Log.v("friendLat: ", friendLat);
//        Log.v("friendLang: ", friendLang);

        // Creating a LatLng object for the current location
        originLatLang = new LatLng(currentLat, currentLang);
        destinationLatLang = new LatLng(Double.parseDouble(friendLat), Double.parseDouble(friendLang));

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(originLatLang, destinationLatLang, mode);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

//        setHasOptionsMenu(true);

        return rootView;
    }

    public void setupNewLocation(){
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Log.v("PROVIDER", "GPS: " + LocationManager.GPS_PROVIDER);
        Log.v("PROVIDER", "Passive: "+LocationManager.PASSIVE_PROVIDER);
        Log.v("PROVIDER", "Network: " + LocationManager.NETWORK_PROVIDER);
        Criteria criteria=new Criteria();
        String provider=locationManager.getBestProvider(criteria, false);
        Log.v("PROVIDER", "BEST: " + provider);

        Location location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            currentLat = location.getLatitude();
            currentLang = location.getLongitude();
        }
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
            currentAddress = snippet;
        } catch (IOException e) {
            e.printStackTrace();
        }


        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_bg);


        MarkerOptions a = new MarkerOptions()
                .position(latLng).title("You are here")
                .position(latLng)
                .snippet(snippet)
                .icon(icon);
        currentMarker = map.addMarker(a);
    }



    public void initMap(Bundle savedInstanceState){
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

//        m.setPosition(new LatLng(50,5));
//
//        currentMarker = new MarkerOptions().position(latLng)
//                .title("You are here")
//                .position(latLng)
//                .snippet(snippet)
//                .icon(icon);
//
//        map.addMarker(currentMarker);

        }

//    @Override
//    public void onStop() {
//        if(ttobj !=null){
//            ttobj.stop();
//            ttobj.shutdown();
//        }
//        super.onStop();
//    }

    @Override
    public void onPause() {

        locationManager.removeUpdates(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        mapView.onResume();
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

        setupNewLocation();

        super.onResume();


//        DirectionsAPITask task = new DirectionsAPITask();
//        task.execute(new String[]{"https://maps.googleapis.com/maps/api/directions/json?origin="+currentLat+currentLang+"&destination="+friendLat+friendLang+"&key=AIzaSyBN5lfLlPwxC1DWbmrySr9zT0FIvhjWKxI"});
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        speaker.destroy();
    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onLocationChanged(Location location) {
        // Getting latitude of the current location
        double latitude = location.getLatitude();
        currentLat = latitude;

        // Getting longitude of the current location
        double longitude = location.getLongitude();
        currentLang = longitude;

        LatLng latLng = new LatLng(currentLat, currentLang);
        currentMarker.setPosition(latLng);

/**  Calculating Total Distance ***/
        Location locationA = new Location("point A");

        locationA.setLatitude(currentLat);
        locationA.setLongitude(currentLang);

        Location locationB = new Location("point B");




        locationB.setLatitude(Double.parseDouble(friendLat));
        locationB.setLongitude(Double.parseDouble(friendLang));

        float totalDistance = locationA.distanceTo(locationB);
        int totalresult =(int) totalDistance;
        String totalDistanceString = totalresult+" meters to "+friendName;
        if(totalDistance>1000){
            float kms = totalresult/1000;
            totalDistanceString = String.format("%.2f", kms)+" kilometers to "+friendName;
        }


        if(mPolyLine!=null){
            List<LatLng> polyLinePoints = mPolyLine.getPoints();

            Location currentPoint = new Location("current Point");

            currentPoint.setLatitude(currentLat);
            currentPoint.setLongitude(currentLang);

            Location polyLinePoint = new Location("point");

            polyLinePoint.setLatitude(polyLinePoints.get(0).latitude);
            polyLinePoint.setLongitude(polyLinePoints.get(0).longitude);

            float pointDistance = currentPoint.distanceTo(polyLinePoint);
            int pointResult =(int) pointDistance;

            if(pointResult<10){
                polyLinePoints.remove(0);
            }

            mPolyLine.setPoints(polyLinePoints);

            if(nextPointDistance==0){
                nextPointDistance = pointResult;
                stray = 0;
            }
            else{
                if(nextPointDistance<pointResult){
                    nextPointDistance = pointResult;
                    stray++;
                    if(stray>4){
                        originLatLang = new LatLng(currentLat, currentLang);
                        destinationLatLang = new LatLng(Double.parseDouble(friendLat), Double.parseDouble(friendLang));

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(originLatLang, destinationLatLang, mode);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);

                        stray = 0;
                        nextPointDistance = 0;
                    }
                }
                else if(nextPointDistance>=pointResult){
                    stray = 0;
                    nextPointDistance = 0;
                }
            }
        }









        if(endPoints.size()>0){
            String totalDurationString = "Total Duration: "+totalDuration;

            getActivity().setTitle(Html.fromHtml("<small>" + totalDuration + " & " + totalDistanceString + "</small>"));

        Location locationC = new Location("point A");

            locationC.setLatitude(currentLat);
            locationC.setLongitude(currentLang);

        Location locationD = new Location("point B");


/** INDEX OUT OF BOUND EXCEPTION ON REACHING DESTINATION**
 *
 *
 *
 */
            locationD.setLatitude(endPoints.get(endPointPosition).latitude);
            locationD.setLongitude(endPoints.get(endPointPosition).longitude);

            float distance = locationC.distanceTo(locationD);
            int result =(int) distance;

            String noHtml = "";

            if (endPointPosition+1!=maneuvers.size()){
                String instruction = +result+" meters";
                if(distance>1000){
                    float kms = result/1000;
                    instruction =String.format("%.2f", kms)+" kilometers";
                }

                instrunctionsTextView.setText(instruction);
                if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("turn-sharp-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_sharp_left));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("uturn-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.uturn_right));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("turn-slight-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_slight_right));
                }
//                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("merge")){
//                    maneuverImageView.setImageResource(getResources().getDrawable(R.drawable.me));
//                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("roundabout-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.roundabout_left));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("roundabout-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.roundabout_right));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("uturn-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.uturn_left));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("turn-slight-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_slight_left));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("turn-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_left));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("ramp-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.ramp_right));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("turn-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_right));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("fork-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.fork_right));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("straight")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.straight));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("fork-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.fork_left));
                }
//                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("ferry-train")){
//                    maneuverImageView.setImageResource(getResources().getDrawable(R.drawable.fer));
//                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("turn-sharp-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_sharp_right));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("ferry")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.ferry));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("ramp-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.ramp_left));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("keep-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.keep_left));
                }
                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("keep-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.keep_right));
                }


                String htmlString = html.get(endPointPosition + 1);
                noHtml = htmlString.replaceAll("<[^>]*>", "");

            }
            else{
                String instruction =+result+" meters";
                if(distance>1000){
                    float kms = result/1000;
                    instruction =String.format("%.2f", kms)+" kilometers";
                }

                instrunctionsTextView.setText(instruction);
                if(maneuvers.get(endPointPosition).equalsIgnoreCase("turn-sharp-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_sharp_left));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("uturn-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.uturn_right));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("turn-slight-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_slight_right));
                }
//                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("merge")){
//                    maneuverImageView.setImageResource(getResources().getDrawable(R.drawable.me));
//                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("roundabout-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.roundabout_left));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("roundabout-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.roundabout_right));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("uturn-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.uturn_left));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("turn-slight-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_slight_left));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("turn-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_left));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("ramp-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.ramp_right));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("turn-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_right));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("fork-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.fork_right));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("straight")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.straight));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("fork-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.fork_left));
                }
//                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("ferry-train")){
//                    maneuverImageView.setImageResource(getResources().getDrawable(R.drawable.fer));
//                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("turn-sharp-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_sharp_right));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("ferry")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.ferry));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("ramp-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.ramp_left));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("keep-left")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.keep_left));
                }
                else if(maneuvers.get(endPointPosition).equalsIgnoreCase("keep-right")){
                    maneuverImageView.setBackground(getResources().getDrawable(R.drawable.keep_right));
                }
                String htmlString = html.get(endPointPosition);
                noHtml = htmlString.replaceAll("<[^>]*>", "");
            }




        if(distance<10){
            endPointPosition++;

//            ttobj.speak(noHtml, TextToSpeech.QUEUE_FLUSH, null);
            if (speaker!=null){
                speaker.speak(noHtml);
            }

            tvDistanceDuration.setText(noHtml);
        }




        Log.v("NavFrag", "Total Distance in meters: "+distance);

        }

        // Creating a LatLng object for the current location

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public void closeNavigation(){
        final Firebase ref = new Firebase(Constants.BASE_URL);
        Firebase postRef = ref.child("users").child(userId).child("activeNavigation");
        postRef.setValue(null);

        String navigationMode = "";
        if(mode.contains("driving")){
            navigationMode = "driving";
        }
        else if(mode.contains("walking")){
            navigationMode = "walking";
        }
        else if(mode.contains("bicycling")){
            navigationMode = "bicycling";
        }

        Firebase myNavHistoryRef = ref.child("users").child(userId).child("navigationHistory").child(myNavHistReference);

        Map<String, Object> post1 = new HashMap<String, Object>();
        post1.put("navEndTime", Long.toString(System.currentTimeMillis()));
        post1.put("totalDistance", totalDistance);
        post1.put("totalDuration", totalDuration);
        post1.put("status", "completed");
        post1.put("mode", navigationMode);


        myNavHistoryRef.updateChildren(post1);

        Firebase friendNavHistoryRef = ref.child("users").child(friendId).child("navigationHistory").child(friendHistReference);

        Map<String, Object> post2 = new HashMap<String, Object>();
        post2.put("navEndTime", Long.toString(System.currentTimeMillis()));
        post2.put("totalDistance", totalDistance);
        post2.put("totalDuration", totalDuration);
        post2.put("status", "completed");
        post2.put("mode", navigationMode);

        friendNavHistoryRef.updateChildren(post2);
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest, String mode)
    {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";


        // Sensor enabled
        String APIKey = "key=AIzaSyAm3zFVDshvK5054kyeNM9TPdbIFGtQuhs";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode + "&" + APIKey;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try
        {
            URL url = new URL(strUrl);
            Log.v("NavFrag", "Directions URL: "+strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e)
        {
            Log.d("downloading url error", e.toString());
        } finally
        {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>
    {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url)
        {

            // For storing data from web service
            String data = "";

            Log.v("NavFrag", "Download Task DoInBackGround");

            try
            {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e)
            {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
    {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData)
        {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try
            {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result)
        {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";
            maneuvers.clear();
            endPoints.clear();
            html.clear();


            if (result.size() < 1)
            {
                Toast.makeText(getActivity().getApplicationContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                if(i==0) {

                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();



                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {

                        HashMap<String, String> point = path.get(j);

                        if (j == 0) { // Get distance from the list
                            distance = point.get("distance");
                            totalDistance = distance;
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            duration = point.get("duration");
                            totalDuration = duration;
                            continue;
                        }

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
//                    lineOptions.addAll(points);
//                    lineOptions.width(10);
//                    lineOptions.color(getResources().getColor(R.color.wallet_holo_blue_light));

//                    tvDistanceDuration.setText("Distance:" + distance + ", Duration:" + duration);

                    // Drawing polyline in the Google Map for the i-th route
                    if (mPolyLine!=null){
                        mPolyLine.remove();
                    }


                    mPolyLine = map.addPolyline(new PolylineOptions().addAll(points).width(10).color(getResources().getColor(R.color.wallet_holo_blue_light)));
//                    mPolyLine.getPoints()
//                    mPolyLine;
//                    map.addPolyline(lineOptions);
                    continue;

                }
                else if (i == 1) {
                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {

                        HashMap<String, String> point = path.get(j);
                        maneuvers.add(point.get("maneuver"));
                    }

                    continue;

                }

                else if (i == 2) {
                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {

                        HashMap<String, String> point = path.get(j);
                        double endlat = Double.parseDouble(point.get("endLat"));
                        double endlng = Double.parseDouble(point.get("endLang"));
                        LatLng endPosition = new LatLng(endlat, endlng);
                        endPoints.add(endPosition);
                    }
                    continue;

                }

                else if (i == 3) {
                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        html.add(point.get("HTMLInstruction"));
                    }

                    continue;

                }
            }


            if(endPoints.size()>0) {

                Location locationA = new Location("point A");

                locationA.setLatitude(currentLat);
                locationA.setLongitude(currentLang);

                Location locationB = new Location("point B");


                locationB.setLatitude(endPoints.get(endPointPosition).latitude);
                locationB.setLongitude(endPoints.get(endPointPosition).longitude);

                float distance22 = locationA.distanceTo(locationB);
                int result22 =(int) distance22;


                if (endPointPosition+1!=maneuvers.size()){
                    String instruction = result22+" meters";
                    if(distance22>1000){
                        float kms = result22/1000;
                        instruction = String.format("%.2f", kms)+" kilometers";
                    }

                    instrunctionsTextView.setText(instruction);
                    if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("turn-sharp-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_sharp_left));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("uturn-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.uturn_right));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("turn-slight-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_slight_right));
                    }
//                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("merge")){
//                    maneuverImageView.setImageResource(getResources().getDrawable(R.drawable.me));
//                }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("roundabout-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.roundabout_left));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("roundabout-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.roundabout_right));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("uturn-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.uturn_left));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("turn-slight-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_slight_left));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("turn-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_left));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("ramp-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.ramp_right));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("turn-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_right));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("fork-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.fork_right));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("straight")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.straight));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("fork-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.fork_left));
                    }
//                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("ferry-train")){
//                    maneuverImageView.setImageResource(getResources().getDrawable(R.drawable.fer));
//                }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("turn-sharp-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_sharp_right));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("ferry")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.ferry));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("ramp-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.ramp_left));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("keep-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.keep_left));
                    }
                    else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("keep-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.keep_right));
                    }
                    String htmlString = html.get(endPointPosition+1);
                    String noHtml = htmlString.replaceAll("<[^>]*>", "");
                    if (speaker!=null){
                        speaker.speak(noHtml);
                    }

                    tvDistanceDuration.setText(noHtml);


                }
                else{
                    String instruction = result22+" meters";
                    if(distance22>1000){
                        float kms = result22/1000;
                        instruction = String.format("%.2f", kms)+" kilometers";
                    }

                    instrunctionsTextView.setText(instruction);
                    if(maneuvers.get(endPointPosition).equalsIgnoreCase("turn-sharp-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_sharp_left));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("uturn-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.uturn_right));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("turn-slight-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_slight_right));
                    }
//                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("merge")){
//                    maneuverImageView.setImageResource(getResources().getDrawable(R.drawable.me));
//                }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("roundabout-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.roundabout_left));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("roundabout-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.roundabout_right));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("uturn-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.uturn_left));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("turn-slight-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_slight_left));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("turn-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_left));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("ramp-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.ramp_right));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("turn-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_right));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("fork-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.fork_right));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("straight")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.straight));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("fork-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.fork_left));
                    }
//                else if(maneuvers.get(endPointPosition+1).equalsIgnoreCase("ferry-train")){
//                    maneuverImageView.setImageResource(getResources().getDrawable(R.drawable.fer));
//                }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("turn-sharp-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.turn_sharp_right));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("ferry")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.ferry));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("ramp-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.ramp_left));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("keep-left")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.keep_left));
                    }
                    else if(maneuvers.get(endPointPosition).equalsIgnoreCase("keep-right")){
                        maneuverImageView.setBackground(getResources().getDrawable(R.drawable.keep_right));
                    }
                    String htmlString = html.get(endPointPosition);
                    String noHtml = htmlString.replaceAll("<[^>]*>", "");
                    if (speaker!=null){
                        speaker.speak(noHtml);
                    }
                    tvDistanceDuration.setText(noHtml);

                }

                /**  Calculating Total Distance ***/
                Location locationC = new Location("point A");

                locationC.setLatitude(currentLat);
                locationC.setLongitude(currentLang);

                Location locationD = new Location("point B");




                locationD.setLatitude(Double.parseDouble(friendLat));
                locationD.setLongitude(Double.parseDouble(friendLang));

                float totalDistance = locationC.distanceTo(locationD);
                int totalresult =(int) totalDistance;
                String totalDistanceString = totalresult+" meters to "+friendName;
                if(totalDistance>1000){
                    float kms = totalresult/1000;
                    totalDistanceString = String.format("%.2f", kms)+" kilometers to "+friendName;
                }


                String totalDurationString = "Total Duration: "+totalDuration;

                getActivity().setTitle(Html.fromHtml("<small>" + totalDuration + " & " + totalDistanceString + "</small>"));








                Log.v("NavFrag", "F9erst Distance in meters: " + distance22);


            }



        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu.hasVisibleItems()){
            super.onCreateOptionsMenu(menu, inflater);
//            this.mMenu = menu;
        }
        else{
            inflater.inflate(R.menu.menu_navigation_fragment, menu);
        }
        this.mMenu = menu;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_mode:
            {
                if(walkButton.getVisibility()==View.GONE){
                    walkButton.setVisibility(View.VISIBLE);
                    bikeButton.setVisibility(View.VISIBLE);
                    carButton.setVisibility(View.VISIBLE);
                }
                else{
                    walkButton.setVisibility(View.GONE);
                    bikeButton.setVisibility(View.GONE);
                    carButton.setVisibility(View.GONE);
                }
                //show rest of modes
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
