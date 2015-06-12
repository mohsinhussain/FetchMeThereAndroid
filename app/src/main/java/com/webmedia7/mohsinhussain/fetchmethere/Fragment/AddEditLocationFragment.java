package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
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
import com.webmedia7.mohsinhussain.fetchmethere.Adapters.GalleryImageAdapter;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class AddEditLocationFragment extends Fragment {

    public AddEditLocationFragment(){}

//    private OnFragmentInteractionListener mListener;

    MapView mapView;
    GoogleMap map;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    String name = "Current Location";
    String address;
    private double lat;
    private double lang;
    Button addLocation;
    EditText nameEditText, addressEditText, latEditText, langEditText;
    SharedPreferences preferenceSettings;
    String userId = "";
    String refId = "";
    ProgressDialog ringProgressDialog;
    Marker currentMarker;
    ArrayList<String> imagesStringArray = new ArrayList<String>();;
    //gallery object
    private Gallery picGallery;
    final int PIC_CROP = 2;
    int REQUEST_CAMERA = 999;
    int SELECT_FILE = 888;
    private GalleryImageAdapter mAdapter;
    int galleryItemSelectedPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);

        userId = preferenceSettings.getString("userId", null);

        View rootView = inflater.inflate(R.layout.fragment_add_location, container, false);

        mapView = (MapView) rootView.findViewById(R.id.mapview_add_edit_location);

        imagesStringArray = new ArrayList<String>();


        //get the gallery view
        picGallery = (Gallery) rootView.findViewById(R.id.gallery);



        picGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectImage(position);
            }
        });

        nameEditText = (EditText) rootView.findViewById(R.id.name_edit_text);
        addressEditText = (EditText) rootView.findViewById(R.id.address_edit_text);
        latEditText = (EditText) rootView.findViewById(R.id.lat_edit_text);
        langEditText = (EditText) rootView.findViewById(R.id.lang_edit_text);
        addLocation = (Button) rootView.findViewById(R.id.addLocation);

        Bundle arguments = getArguments();
        if (arguments!=null && getArguments().containsKey("action")){
            if (getArguments().getString("action").equalsIgnoreCase("edit"))
            {
                Bundle bundle = this.getArguments();
                name = bundle.getString("name", null);
                address = bundle.getString("address", null);
                refId = bundle.getString("refId", null);
                lat = bundle.getDouble("lat");
                lang = bundle.getDouble("lang");
                imagesStringArray = bundle.getStringArrayList("imagesArray");
                if(imagesStringArray==null){
                    imagesStringArray= new ArrayList<String>();
                }


                nameEditText.setText(name);
                addressEditText.setText(address);
                latEditText.setText(Double.toString(lat));
                langEditText.setText(Double.toString(lang));
                addLocation.setText("SAVE LOCATION");

                addLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Saving Location...", true);
                        ringProgressDialog.setCancelable(true);

                        final Firebase ref = new Firebase(Constants.BASE_URL);
                        Firebase postRef = ref.child("users").child(userId).child("myLocations").child(refId);

                        Map<String, Object> post1 = new HashMap<String, Object>();
                        post1.put("Name", nameEditText.getText().toString());
                        post1.put("Address", addressEditText.getText().toString());
                        post1.put("lat", latEditText.getText().toString());
                        post1.put("lang", langEditText.getText().toString());
                        post1.put("imagesArray", imagesStringArray);

                        postRef.setValue(post1, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                ringProgressDialog.dismiss();
                                if (firebaseError != null) {
                                    Toast.makeText(getActivity(), "Data could not be saved. " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    getFragmentManager().popBackStack();
                                    getFragmentManager().popBackStack();
                                    System.out.println("Data saved successfully.");
                                }
                            }
                        });

                    }
                });
            }
        }
        else
        {
            name = "Current Location";
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager
                    .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (location != null) {
                lat = location.getLatitude();
                lang = location.getLongitude();
            }
            Geocoder geocoder;
            String snippet = "Unknown";

            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(lat, lang, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                if(addresses.size()>0) {
                    snippet = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName(); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    address = snippet;
//                    addressEditText.setText(snippet); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            nameEditText.setText(name);
            addressEditText.setText(address);
            latEditText.setText(Double.toString(lat));
            langEditText.setText(Double.toString(lang));

            addLocation.setText("ADD LOCATION");

            addLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Adding Location...", true);
                    ringProgressDialog.setCancelable(true);

                    final Firebase ref = new Firebase(Constants.BASE_URL);
                    Firebase postRef = ref.child("users").child(userId).child("myLocations");

                    Map<String, Object> post1 = new HashMap<String, Object>();
                    post1.put("Name", nameEditText.getText().toString());
                    post1.put("Address", addressEditText.getText().toString());
                    post1.put("lat", latEditText.getText().toString());
                    post1.put("lang", langEditText.getText().toString());
                    post1.put("imagesArray", imagesStringArray);

                    postRef.push().setValue(post1, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            ringProgressDialog.dismiss();
                            if (firebaseError != null) {
                                Toast.makeText(getActivity(), "Data could not be saved. " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                getFragmentManager().popBackStack();
                                System.out.println("Data saved successfully.");
                            }
                        }
                    });

                }
            });
        }

        mAdapter = new GalleryImageAdapter(getActivity(), imagesStringArray, "", this);

        picGallery.setAdapter(mAdapter);

        initMap(savedInstanceState);

        return rootView;
    }

    public void initMap(Bundle savedInstanceState){
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(false);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        LatLng latLng = new LatLng(lat, lang);

        // Showing the current location in Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Zoom in the Google Map
        map.animateCamera(CameraUpdateFactory.zoomTo(15));


        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_bg);


        MarkerOptions a = new MarkerOptions()
                .title("You are here")
                .position(latLng)
                .snippet(address)
                .draggable(true)
                .icon(icon);

        currentMarker = map.addMarker(a);

        Bundle arguments = getArguments();
        if (arguments!=null && getArguments().containsKey("action")) {
            if (getArguments().getString("action").equalsIgnoreCase("edit")) {

                currentMarker.setTitle(getArguments().getString("name"));
            }
        }



        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();
                lat = latLng.latitude;
                lang = latLng.longitude;
                updateAddress();

            }
        });
    }

    public void updateAddress(){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lang, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getCountryName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        addressEditText.setText(address);
        currentMarker.setSnippet(address);
        latEditText.setText(Double.toString(lat));
        langEditText.setText(Double.toString(lang));
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    public void selectImage(final int position) {
        final CharSequence[] items;
        if (imagesStringArray==null){
            imagesStringArray = new ArrayList<String>();
        }
        if(position==imagesStringArray.size()){
            items = new CharSequence[]{"Take Photo", "Choose from Library",
                    "Cancel"};
        }
        else{
            items = new CharSequence[]{"Take Photo", "Choose from Library", "Remove Photo",
                    "Cancel"};
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {

                    Intent intent = new Intent();
                    // call android default gallery
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    // ******** code for crop image
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 0);
                    intent.putExtra("aspectY", 0);
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 200);

                    try {

                        intent.putExtra("return-data", true);
                        startActivityForResult(Intent.createChooser(intent,
                                "Complete action using"), SELECT_FILE);

                    } catch (ActivityNotFoundException e) {
                        // Do nothing for now
                    }
                }
                else if (items[item].equals("Remove Photo")) {
                    imagesStringArray.remove(position);
                    mAdapter.notifyDataSetChanged();
                }
                else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void performCrop(Uri picUri){
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                //get the Uri for the captured image
                Uri picUri = data.getData();
                performCrop(picUri);

            } else if (requestCode == SELECT_FILE) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                setProfileImageFile(thumbnail);
            }
            else if(requestCode == PIC_CROP){
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setProfileImageFile(thumbnail);
            }
        }
    }

    public void setProfileImageFile(Bitmap bmp){
        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
        byte[] byteArray = bYtE.toByteArray();
        String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        imagesStringArray.add(imageString);

        mAdapter.notifyDataSetChanged();


        //Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata .length); function to decode
    }



}
