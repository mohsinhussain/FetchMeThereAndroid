package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.RoundedImageView;
import com.webmedia7.mohsinhussain.fetchmethere.FetchMeThere;
import com.webmedia7.mohsinhussain.fetchmethere.R;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class NavigationHistoryDetailFragment extends Fragment {

    public NavigationHistoryDetailFragment(){}
    MapView mapView, objMapView;
    GoogleMap map, objMap;
    private double lat;
    private double lang;
    String name, address, refId;
    TextView destAddressTextView, destLatTextView, destLangTextView, friendName, destinationDescTextView,
            objAddressTextView, objLatTextView, objLangTextView, objDescTextView, destLocNameTextView, activeTextView, objLocNameTexView, timeCompletedTextView, distanceCompletedTextView;
    String friendId;
    Button locationButton;
    EditText commentsEditText;
    ProgressDialog ringProgressDialog;
//    private OnFragmentInteractionListener mListener;
    String userId = "";
    String myDisplayName = "";
    SharedPreferences preferenceSettings;
    LinearLayout mainLinearLayout, furtherDetailLayout;
    RoundedImageView profile_image_view;
    String destAddressString, destLatString, destLangString, destLocNameString, objAddressString, objLatString, objLangString, objLocNameString;
    String friendProfileImageString = "";
    String navMode = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_navigation_history_detail, container, false);

        destAddressTextView = (TextView) rootView.findViewById(R.id.addressTextView);
        destLatTextView = (TextView) rootView.findViewById(R.id.latTextView);
        destLangTextView = (TextView) rootView.findViewById(R.id.lang_TextView);
        objAddressTextView = (TextView) rootView.findViewById(R.id.object_addressTextView);
        objLatTextView = (TextView) rootView.findViewById(R.id.object_latTextView);
        objLangTextView = (TextView) rootView.findViewById(R.id.object_longTextView);
        mapView = (MapView) rootView.findViewById(R.id.mapview_add_location);
        objMapView = (MapView) rootView.findViewById(R.id.object_mapview_add_location);
        locationButton = (Button) rootView.findViewById(R.id.location_button);
        destinationDescTextView = (TextView) rootView.findViewById(R.id.description_text_view);
        objDescTextView = (TextView) rootView.findViewById(R.id.object_desc_text_view);
        activeTextView = (TextView) rootView.findViewById(R.id.activeTextView);
        friendName = (TextView) rootView.findViewById(R.id.nameTextView);
        profile_image_view = (RoundedImageView) rootView.findViewById(R.id.profile_image_view);
        destLocNameTextView = (TextView) rootView.findViewById(R.id.locaNameTextView);
        objLocNameTexView = (TextView) rootView.findViewById(R.id.object_locaNameTextView);
        timeCompletedTextView = (TextView) rootView.findViewById(R.id.timeValueTextView);
        distanceCompletedTextView = (TextView) rootView.findViewById(R.id.distanceValueTextView);
        commentsEditText = (EditText) rootView.findViewById(R.id.commentEditText);
        commentsEditText.setVisibility(View.GONE);
        mainLinearLayout = (LinearLayout) rootView.findViewById(R.id.main_linear_layout);
        furtherDetailLayout = (LinearLayout) rootView.findViewById(R.id.furtherDetailLayout);

        preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
        userId = preferenceSettings.getString("userId", null);
        myDisplayName = preferenceSettings.getString("displayName", null);







        Bundle bundle = this.getArguments();

        if(bundle.containsKey("action")) {
            friendName.setText(bundle.getString("friendName", null));
            friendProfileImageString = bundle.getString("profileImageString", null);
            Constants.setImageViewFromString(friendProfileImageString, profile_image_view);
            if (bundle.containsKey("mode")){
                navMode=bundle.getString("mode");
            }
            if (bundle.getString("action", null).equalsIgnoreCase("object")) {

                destLocNameString = bundle.getString("friendLocName", null);
                destAddressString = bundle.getString("friendAddress", null);
                destLatString = "Lat: "+bundle.getString("friendLat", null);
                destLangString = "Lang: "+bundle.getString("friendLang", null);
                objLocNameString = "Current Location";
                objAddressString = bundle.getString("myAddress", null);
                objLatString = "Lat: "+bundle.getString("myLat", null);
                objLangString = "Lang: "+bundle.getString("myLang", null);

                destinationDescTextView.setText("Requested location of "+bundle.getString("friendName", null));
                destAddressTextView.setText(destAddressString);
                destLocNameTextView.setText(destLocNameString);
                destLatTextView.setText(destLatString);
                destLangTextView.setText(destLangString);


                objDescTextView.setText("My starting location: ");
                objLocNameTexView.setText(objLocNameString);
                objAddressTextView.setText(objAddressString);
                objLatTextView.setText(objLatString);
                objLangTextView.setText(objLangString);





            } else {
                objDescTextView.setText(bundle.getString("friendName", null)+" starting location:");
                objAddressTextView.setText(bundle.getString("friendAddress", null));
                objLocNameTexView.setText("Current Location");
                objLatTextView.setText("Lat: "+bundle.getString("friendLat", null).toString());
                objLangTextView.setText("Lang: "+bundle.getString("friendLang", null).toString());


                destinationDescTextView.setText("My location sent to " + bundle.getString("friendName", null));
                destLocNameTextView.setText("Current Location");
                destAddressTextView.setText(bundle.getString("myAddress", null));
                destLatTextView.setText("Lat: "+bundle.getString("myLat", null).toString());
                destLangTextView.setText("Lang: "+bundle.getString("myLang", null).toString());
            }

            if(bundle.getString("status", null).equalsIgnoreCase("active")){
                furtherDetailLayout.setVisibility(View.GONE);
                activeTextView.setVisibility(View.VISIBLE);
                activeTextView.setText("Navigation is still ACTIVE!");
            }
            else{
                if(!navMode.equalsIgnoreCase("")){
                    activeTextView.setVisibility(View.VISIBLE);
                    activeTextView.setText("Navigation mode is "+navMode);
                }

                distanceCompletedTextView.setText(bundle.getString("totalDistance", null));
                timeCompletedTextView.setText(bundle.getString("totalDuration", null));
            }

        }

        initMap(savedInstanceState);

        setHasOptionsMenu(true);

        return rootView;
    }



    public void initMap(Bundle savedInstanceState){
        mapView.onCreate(savedInstanceState);
        objMapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(false);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        objMap = objMapView.getMap();
        objMap.getUiSettings().setMyLocationButtonEnabled(false);
        objMap.setMyLocationEnabled(false);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        Bundle bundle = this.getArguments();
        if(bundle.containsKey("action")) {
            if (bundle.getString("action", null).equalsIgnoreCase("object")) {
                LatLng destLatLang = new LatLng(Double.parseDouble(getArguments().getString("friendLat")), Double.parseDouble(getArguments().getString("friendLang")));
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_bg);
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(getArguments().getString("friendName"))
                        .position(destLatLang)
                        .icon(icon);

                map.addMarker(markerOptions);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(destLatLang, 16);
                map.animateCamera(cameraUpdate);


                LatLng objLatLang = new LatLng(Double.parseDouble(getArguments().getString("myLat")), Double.parseDouble(getArguments().getString("myLang")));
                markerOptions.position(objLatLang);

                objMap.addMarker(markerOptions);
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(objLatLang, 16);
                objMap.animateCamera(cameraUpdate);
            }
            else{
                LatLng destLatLang = new LatLng(Double.parseDouble(getArguments().getString("friendLat")), Double.parseDouble(getArguments().getString("friendLang")));
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_bg);
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(getArguments().getString("friendName"))
                        .position(destLatLang)
                        .icon(icon);

                objMap.addMarker(markerOptions);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(destLatLang, 16);
                objMap.animateCamera(cameraUpdate);


                LatLng objLatLang = new LatLng(Double.parseDouble(getArguments().getString("myLat")), Double.parseDouble(getArguments().getString("myLang")));
                markerOptions.position(objLatLang);

                map.addMarker(markerOptions);
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(objLatLang, 16);
                map.animateCamera(cameraUpdate);
            }

        }


    }

    @Override
    public void onResume() {
        mapView.onResume();
        objMapView.onResume();
        super.onResume();
        Tracker t = FetchMeThere.getInstance().tracker;
        t.setScreenName("Navigation History Detail");
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        objMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        objMapView.onLowMemory();
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onEditLocation(String name, String address, double lat, double lang, ArrayList<String> imagesArray, String refId);
//        public void onLocationSent(String friendName, String locationName);
//        public void onRequestLocationSent(String friendName);
//    }

}
