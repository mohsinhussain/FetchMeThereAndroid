package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.RoundedImageView;
import com.webmedia7.mohsinhussain.fetchmethere.FetchMeThere;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class ProfileFragment extends Fragment {

    public ProfileFragment(){}

    ImageView headerImageView;
    RoundedImageView profileImageView;
    TextView headerTextView, profileImageTextView, mobileNumberTextView;
    EditText firstNameEditText, lastNameEditText, emailEditText;
    Button saveProfileButton;
    LinearLayout uploadPictureLayout;

    private SharedPreferences preferenceSettings;
    private SharedPreferences.Editor preferenceEditor;

    private String displayName = "";
    private String profileImageString = "";
    private String emailString = "";
    private String firstNameString = "";
    private String lastNameString = "";
    private String userId = "";
    private String mobileNumberString = "";
    String profileImageFile = "";
    ProgressDialog ringProgressDialog;
    int REQUEST_CAMERA = 999;
    int SELECT_FILE = 888;
    final int PIC_CROP = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        headerImageView = (ImageView) rootView.findViewById(R.id.header_image_view);
        profileImageView = (RoundedImageView) rootView.findViewById(R.id.profile_image_view);
        headerTextView = (TextView) rootView.findViewById(R.id.header_text_view);
        profileImageTextView = (TextView) rootView.findViewById(R.id.button_text_view);
        mobileNumberTextView = (TextView) rootView.findViewById(R.id.mobile_number_text_view);
        firstNameEditText = (EditText) rootView.findViewById(R.id.first_name_edit_text);
        lastNameEditText = (EditText) rootView.findViewById(R.id.last_name_edit_text);
        emailEditText = (EditText) rootView.findViewById(R.id.email_edit_text);
        saveProfileButton = (Button) rootView.findViewById(R.id.next_step_button);
        uploadPictureLayout = (LinearLayout) rootView.findViewById(R.id.app_picture_layout);

        preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
        displayName = preferenceSettings.getString("displayName", null);
        profileImageString = preferenceSettings.getString("profileImageString", null);
        emailString = preferenceSettings.getString("email", null);
        firstNameString = preferenceSettings.getString("firstName", null);
        lastNameString = preferenceSettings.getString("lastName", null);
        mobileNumberString = preferenceSettings.getString("mobileNumber", null);
        userId = preferenceSettings.getString("userId", null);


        setLayout();


        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(firstNameEditText.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getActivity(), "Please provide first name!", Toast.LENGTH_LONG).show();
                    firstNameEditText.setHintTextColor(Color.parseColor("red"));
                    firstNameEditText.requestFocus();
                    return;
                }
                else if(lastNameEditText.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getActivity(), "Please provide last name!", Toast.LENGTH_LONG).show();
                    lastNameEditText.setHintTextColor(Color.parseColor("red"));
                    lastNameEditText.requestFocus();
                    return;
                }
//                else if(countrycodeString.equalsIgnoreCase("")) {
//                    Toast.makeText(getApplicationContext(), "Please provide country code!", Toast.LENGTH_LONG).show();
////                    countryCodeSpinner.setHighlightColor(Color.parseColor("red"));
//                    countryCodeSpinner.requestFocus();
//                    return;
//                }

                preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
                preferenceEditor = preferenceSettings.edit();
//                preferenceEditor.putString("userId", authData.getUid());
                preferenceEditor.putString("email", emailEditText.getText().toString());
                preferenceEditor.putString("firstName", firstNameEditText.getText().toString());
                preferenceEditor.putString("lastName", lastNameEditText.getText().toString());
                preferenceEditor.putString("displayName", firstNameEditText.getText().toString());
//                preferenceEditor.putString("mobileNumber", completeMobileNumber);
//                preferenceEditor.putString("selectedUserString", selectedUserString);
                if(!profileImageFile.equalsIgnoreCase("")){
                    preferenceEditor.putString("profileImageString", profileImageString);
                }
                preferenceEditor.commit();

                ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Saving Profile...", true);
                ringProgressDialog.setCancelable(true);

                final Firebase ref = new Firebase(Constants.BASE_URL);
                Firebase postRef = ref.child("users").child(userId);

                Map<String, Object> map = new HashMap<String, Object>();
                if(!emailEditText.getText().toString().equalsIgnoreCase("")){
                    map.put("email", emailEditText.getText().toString());
                }
                map.put("displayName", firstNameEditText.getText().toString());
                map.put("firstName", firstNameEditText.getText().toString());
                map.put("lastName", lastNameEditText.getText().toString());
                if(!profileImageString.equalsIgnoreCase(""))
                {
                    map.put("profileImageString", profileImageString);
                }
                postRef.updateChildren(map, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        ringProgressDialog.dismiss();
                        if (firebaseError != null) {
                            Toast.makeText(getActivity(), "Data could not be saved. " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            getActivity().getFragmentManager().popBackStack();
                            Toast.makeText(getActivity(), "Profile saved successfully. ", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });


        uploadPictureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });




        return rootView;
    }

    public void setLayout(){
        headerTextView.setText(firstNameString +" "+ lastNameString);
        firstNameEditText.setText(firstNameString);
        lastNameEditText.setText(lastNameString);
        emailEditText.setText(emailString);
        mobileNumberTextView.setText(mobileNumberString);
        if(!profileImageString.equalsIgnoreCase("")){
            profileImageTextView.setText("Update Profile Picture");
            headerImageView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
            Constants.setImageViewFromString(profileImageString, profileImageView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Tracker t = FetchMeThere.getInstance().tracker;
        t.setScreenName("Profile");
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
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
                } else if (items[item].equals("Cancel")) {
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
            cropIntent.putExtra("aspectX", 0);
            cropIntent.putExtra("aspectY", 0);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 200);
            cropIntent.putExtra("outputY", 200);
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

    public String getAbsolutePath(Uri uri) {
        System.out.println("URI: " + uri.getAuthority());

        if (uri.getAuthority().contains("media")){
            String[] projection = { MediaStore.MediaColumns.DATA };
            String[] column = { MediaStore.Images.Media.DATA };
            String[] col = {android.provider.MediaStore.Images.ImageColumns.DATA};
            @SuppressWarnings("deprecation")
            Cursor cursor = getActivity().getContentResolver().query(uri, projection,
                    null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else
                return null;
        }
        else{
            System.out.println("Google Drive");
            return null;
        }
    }

    public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

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
                Bitmap thumbnail  = null;
                if(data.hasExtra("data")){
                    thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                }
                else{
                    String selectedImagePath = getAbsolutePath(data.getData());
                    if(selectedImagePath==null){
                        Toast.makeText(getActivity(), "Cannot Use this picture", Toast.LENGTH_LONG).show();
                        return;
                    }
                    thumbnail = (Bitmap)decodeFile(selectedImagePath);
                }
                headerImageView.setVisibility(View.GONE);
                profileImageView.setVisibility(View.VISIBLE);
                profileImageView.setImageBitmap(thumbnail);
                setProfileImageFile(thumbnail);
            }
            else if(requestCode == PIC_CROP){
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File destination = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
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

                headerImageView.setVisibility(View.GONE);
                profileImageView.setVisibility(View.VISIBLE);
                profileImageView.setImageBitmap(thumbnail);
                setProfileImageFile(thumbnail);
            }
            profileImageTextView.setText("UPDATE PROFILE PICTURE");
        }
    }

    public void setProfileImageFile(Bitmap bmp){
        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
        byte[] byteArray = bYtE.toByteArray();
        profileImageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);


        //Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata .length); function to decode
    }
}


