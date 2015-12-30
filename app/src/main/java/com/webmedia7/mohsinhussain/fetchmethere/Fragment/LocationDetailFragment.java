package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.webmedia7.mohsinhussain.fetchmethere.Activities.EnLargedImageViewScreen;
import com.webmedia7.mohsinhussain.fetchmethere.Adapters.NormalGalleryAdapter;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.RoundedImageView;
import com.webmedia7.mohsinhussain.fetchmethere.FetchMeThere;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class LocationDetailFragment extends Fragment {

    public LocationDetailFragment(){}
    MapView mapView;
    GoogleMap map;
    private double lat;
    private double lang;
    ArrayList<String> imagesArray;
    String name, address, refId;
    TextView nameTextView, addressTextView, latTextView, langTextView, friendName;
    String friendId;
    String profileImageString = "";
    Button locationButton, sendLocationButton;
    EditText commentsEditText;
    ProgressDialog ringProgressDialog;
    private OnFragmentInteractionListener mListener;
    String userId = "";
    String myDisplayName = "";
    String myProfileImageString = "";
    SharedPreferences preferenceSettings;
    LinearLayout mainLinearLayout;
    private Gallery picGallery;
    NormalGalleryAdapter mAdapter;
    LinearLayout userDetailLayout;
    RoundedImageView profile_image_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_location_detail, container, false);

        nameTextView = (TextView) rootView.findViewById(R.id.name_text_view);
        addressTextView = (TextView) rootView.findViewById(R.id.addressTextView);
        latTextView = (TextView) rootView.findViewById(R.id.latTextView);
        langTextView = (TextView) rootView.findViewById(R.id.longTextView);
        mapView = (MapView) rootView.findViewById(R.id.mapview_add_location);
        locationButton = (Button) rootView.findViewById(R.id.location_button);
        sendLocationButton = (Button) rootView.findViewById(R.id.send_location_button);
        friendName = (TextView) rootView.findViewById(R.id.nameTextView);
        commentsEditText = (EditText) rootView.findViewById(R.id.commentEditText);
        mainLinearLayout = (LinearLayout) rootView.findViewById(R.id.main_linear_layout);
        profile_image_view = (RoundedImageView) rootView.findViewById(R.id.profile_image_view);
        userDetailLayout = (LinearLayout) rootView.findViewById(R.id.userDetailLayout);
        preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
        userId = preferenceSettings.getString("userId", null);
        myDisplayName = preferenceSettings.getString("displayName", null);
        myProfileImageString = preferenceSettings.getString("profileImageString", null);

        imagesArray  = new ArrayList<String>();

//get the gallery view
        picGallery = (Gallery) rootView.findViewById(R.id.gallery);

        picGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Enlarge ImageView
                Intent enlargeImageIntent = new Intent(getActivity(), EnLargedImageViewScreen.class);
                enlargeImageIntent.putExtra("imageToEnlarge", imagesArray.get(position));
                startActivity(enlargeImageIntent);
            }
        });




        Bundle bundle = this.getArguments();

        if(bundle.containsKey("action")) {
            if (bundle.getString("action", null).equalsIgnoreCase("detail")) {
                setHasOptionsMenu(true);

                commentsEditText.setVisibility(View.GONE);

                name = bundle.getString("name", null);
                address = bundle.getString("address", null);
                refId = bundle.getString("refId", null);
                lat = bundle.getDouble("lat");
                lang = bundle.getDouble("lang");
                imagesArray = bundle.getStringArrayList("imagesArray");

                nameTextView.setText(name);
                addressTextView.setText(address);
                latTextView.setText(Double.toString(lat));
                langTextView.setText(Double.toString(lang));

                locationButton.setText("EDIT LOCATION");
                sendLocationButton.setVisibility(View.VISIBLE);
                locationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mListener != null) {
                            mListener.onEditLocation(name, address, lat, lang, imagesArray, refId);
                        }
                    }
                });

                sendLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mListener != null) {
                            mListener.onSendSavedLocation(name, address, Double.toString(lat), Double.toString(lang));
                        }
                    }
                });

            } else if (bundle.getString("action", null).equalsIgnoreCase("sendCurrentLocation")) {
                userDetailLayout.setVisibility(View.VISIBLE);

                lat = bundle.getDouble("lat");
                lang = bundle.getDouble("lang");
                friendId = bundle.getString("friendId");
                profileImageString = bundle.getString("profileImageString");
                friendName.setText(bundle.getString("friendName"));
                Constants.setImageViewFromString(profileImageString,profile_image_view);

                Geocoder geocoder;
                String snippet = "Unknown";

                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(lat, lang, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if(addresses.size()>0){
                        snippet = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName(); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


                nameTextView.setText("Current Location");
                addressTextView.setText(snippet);
                latTextView.setText(Double.toString(lat));
                langTextView.setText(Double.toString(lang));

                locationButton.setText("SEND LOCATION");
                final String finalSnippet = snippet;
                locationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Sending Location...", true);
                        ringProgressDialog.setCancelable(true);

                        final Firebase ref = new Firebase(Constants.BASE_URL);
                        Firebase postRef = ref.child("users").child(friendId).child("receivedLocations");

                        Map<String, String> post1 = new HashMap<String, String>();
                        post1.put("friendId", userId);
                        post1.put("friendName", myDisplayName);
                        post1.put("locName", "Current Location");
                        post1.put("address", finalSnippet);
                        post1.put("lat", latTextView.getText().toString());
                        post1.put("lang", langTextView.getText().toString());
                        post1.put("profileImageString", myProfileImageString);
                        post1.put("comment", commentsEditText.getText().toString());

                        postRef.push().setValue(post1, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                ringProgressDialog.dismiss();
                                if (firebaseError != null) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Location could not be sent. " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                } else {

                                    System.out.println("Data saved successfully.");
                                    if (mListener != null) {
                                        mListener.onLocationSent(friendName.getText().toString(), "Current Location");
                                        getFragmentManager().popBackStack();
                                        getFragmentManager().popBackStack();
                                    }
                                }
                            }
                        });

                    }
                });
            } else if (bundle.getString("action", null).equalsIgnoreCase("requestLocation")) {
                friendId = bundle.getString("friendId");
                friendName.setText(bundle.getString("friendName"));
                mainLinearLayout.setVisibility(View.GONE);
                nameTextView.setVisibility(View.GONE);
                userDetailLayout.setVisibility(View.VISIBLE);
                profileImageString = bundle.getString("profileImageString");
                Constants.setImageViewFromString(profileImageString, profile_image_view);

                locationButton.setText("SEND LOCATION REQUEST");
                locationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Sending Location Request...", true);
                        ringProgressDialog.setCancelable(true);

                        final Firebase ref = new Firebase(Constants.BASE_URL);
                        Firebase postRef = ref.child("users").child(friendId).child("locationRequests");

                        Map<String, String> post1 = new HashMap<String, String>();
                        post1.put("friendId", userId);
                        post1.put("friendName", myDisplayName);
                        post1.put("comment", commentsEditText.getText().toString());
                        post1.put("profileImageString", myProfileImageString);

                        postRef.push().setValue(post1, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                ringProgressDialog.dismiss();
                                if (firebaseError != null) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Location request could not be sent. " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    System.out.println("Data saved successfully.");
                                    if (mListener != null) {
                                        mListener.onRequestLocationSent(friendName.getText().toString());
                                        getFragmentManager().popBackStack();
                                        getFragmentManager().popBackStack();
                                    }
                                }
                            }
                        });

                    }
                });
            }
        }

        if (imagesArray!=null && imagesArray.size()>0){
            mAdapter = new NormalGalleryAdapter(getActivity(), imagesArray, "", this);

            picGallery.setAdapter(mAdapter);
        }
        else{
            picGallery.setVisibility(View.GONE);
        }


        initMap(savedInstanceState);

        setHasOptionsMenu(true);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Bundle bundle = this.getArguments();
        if(bundle.containsKey("action")) {
            if (bundle.getString("action", null).equalsIgnoreCase("detail")) {
                if (menu.hasVisibleItems()) {
                    super.onCreateOptionsMenu(menu, inflater);
                } else {
                    inflater.inflate(R.menu.menu_location_detail_fragment, menu);
                }
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_location:
            {
                if (mListener!=null){
                    mListener.onActionDeleteLocation(refId, userId);
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void initMap(Bundle savedInstanceState){
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(false);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());
        lat = getArguments().getDouble("lat");
        lang = getArguments().getDouble("lang");
        LatLng latLng = new LatLng(lat, lang);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_bg);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                .title(getArguments().getString("name"))
                .position(latLng)
                .icon(icon);

        map.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        map.animateCamera(cameraUpdate);

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        Tracker t = FetchMeThere.getInstance().tracker;
        t.setScreenName("Location Detail");
        t.send(new HitBuilders.ScreenViewBuilder().build());
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
        public void onEditLocation(String name, String address, double lat, double lang, ArrayList<String> imagesArray, String refId);
        public void onSendSavedLocation(String locName, String address, String lat, String lang);
        public void onLocationSent(String friendName, String locationName);
        public void onRequestLocationSent(String friendName);
        public void onActionDeleteLocation(String refId, String userId);
    }

}
