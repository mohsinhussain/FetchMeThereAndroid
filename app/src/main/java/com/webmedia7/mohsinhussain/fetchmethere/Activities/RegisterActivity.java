package com.webmedia7.mohsinhussain.fetchmethere.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.webmedia7.mohsinhussain.fetchmethere.Classes.CSVReader;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.RoundedImageView;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends Activity {

    private TextView headerTextView;
    private Button nextStepButton;
    private int selectedUserPosition;
    Spinner countryCodeSpinner;
    String countrycodeString = "";
    String mobileNumberString = "";
    String firstNameString = "";
    String lastNameString = "";
    String emailString = "";
    String selectedUserString = "";
    EditText mobileNumberEditText, firstNameEditText, lastNameEditText, emailEditText;
    List<String[]> list;
    int REQUEST_CAMERA = 999;
    int SELECT_FILE = 888;
    ImageView headerImageView, selectImageButton;
    RoundedImageView profileImageView;
    LinearLayout app_picture_layout;
    String profileImageFile = "";
    TextView buttonTextView;
    final int PIC_CROP = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        headerTextView = (TextView) findViewById(R.id.header_text_view);
        nextStepButton = (Button) findViewById(R.id.next_step_button);
        countryCodeSpinner = (Spinner) findViewById(R.id.country_code_spinner);

        mobileNumberEditText = (EditText) findViewById(R.id.mobile_number_edit_text);
        firstNameEditText = (EditText) findViewById(R.id.first_name_edit_text);
        lastNameEditText = (EditText) findViewById(R.id.last_name_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        headerImageView = (ImageView) findViewById(R.id.header_image_view);
        profileImageView = (RoundedImageView) findViewById(R.id.profile_image_view);
        selectImageButton = (ImageView) findViewById(R.id.button_image);
        app_picture_layout = (LinearLayout) findViewById(R.id.app_picture_layout);
        buttonTextView = (TextView) findViewById(R.id.button_text_view);

//        mobileNumberEditText.setNextFocusDownId(R.id.first_name_edit_text);
//        firstNameEditText.setNextFocusDownId(R.id.last_name_edit_text);
//        lastNameEditText.setNextFocusDownId(R.id.email_edit_text);
//        emailEditText.setNextFocusDownId(R.id.first_name_edit_text);

        Intent chooseUserIntent = getIntent();
        selectedUserPosition = chooseUserIntent.getExtras().getInt("selectedUserPosition");

        app_picture_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileNumberString = mobileNumberEditText.getText().toString();
                firstNameString = firstNameEditText.getText().toString();
                lastNameString = lastNameEditText.getText().toString();
                emailString = emailEditText.getText().toString();
                selectedUserString = getIntent().getStringExtra(Constants.CURRENT_USER_TYPE_STRING_INTENT);
                if (selectedUserString.equalsIgnoreCase("p")) {
                    headerTextView.setText("REGISTER AS PRIVATE USER");
                } else if (selectedUserString.equalsIgnoreCase("b")) {
                    headerTextView.setText("REGISTER AS BUSINESS USER");
                } else if (selectedUserString.equalsIgnoreCase("a")) {
                    headerTextView.setText("REGISTER AS AGENT");
                }

                if (mobileNumberString.equalsIgnoreCase("") || firstNameString.equalsIgnoreCase("") || lastNameString.equalsIgnoreCase("") || countrycodeString.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Something is Missing!", Toast.LENGTH_LONG).show();
                    return;
                }


                /*****
                 * *** SEND AN API CALL TO GET A VALIDATION CODE *******
                 * ADD VERIFICATION CODE RECEIVED IN RESPONSE TO INTENTS BELOW
                 * ****/


                Intent mobileVerificationIntent = new Intent(RegisterActivity.this, MobileVerificationActivity.class);
                mobileVerificationIntent.putExtra(Constants.COUNTRY_CODE_STRING_INTENT, countrycodeString);
                mobileVerificationIntent.putExtra(Constants.MOBILE_NUMBER_STRING_INTENT, mobileNumberString);
                mobileVerificationIntent.putExtra(Constants.FIRST_NAME_STRING_INTENT, firstNameString);
                mobileVerificationIntent.putExtra(Constants.LAST_NAME_STRING_INTENT, lastNameString);
                mobileVerificationIntent.putExtra(Constants.EMAIL_STRING_INTENT, emailString);
                mobileVerificationIntent.putExtra(Constants.CURRENT_USER_TYPE_STRING_INTENT, selectedUserString);
                mobileVerificationIntent.putExtra(Constants.PROFILE_IMAGE_STRING_INTENT, profileImageFile);
                mobileVerificationIntent.putExtra(Constants.VERIFICATION_CODE_STRING_INTENT, "");
                startActivity(mobileVerificationIntent);
            }
        });


        String locale = getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry();

        TelephonyManager tm = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();

        populateCountryCodes();


//        assignValidation();


        for(int i=0; i < list.size(); i++)
        {
            if(list.get(i)[0].contains(locale)){
                countryCodeSpinner.setSelection(i);
            }
            if(list.get(i)[1].contains(countryCodeValue)){
                countryCodeSpinner.setSelection(i);
            }

        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }


    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                //get the Uri for the captured image
                Uri picUri = data.getData();
                performCrop(picUri);

            } else if (requestCode == SELECT_FILE) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                headerImageView.setVisibility(View.GONE);
                profileImageView.setVisibility(View.VISIBLE);
                profileImageView.setImageBitmap(thumbnail);
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

                headerImageView.setVisibility(View.GONE);
                profileImageView.setVisibility(View.VISIBLE);
                profileImageView.setImageBitmap(thumbnail);
                setProfileImageFile(thumbnail);
            }
            buttonTextView.setText("UPDATE PROFILE PICTURE");
        }
    }

    public void setProfileImageFile(Bitmap bmp){
        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
        byte[] byteArray = bYtE.toByteArray();
        profileImageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);


        //Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata .length); function to decode
    }



    private void populateCountryCodes(){
        String next[] = {};
        list = new ArrayList<String[]>();

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open("countrycodes.csv")));
            for(;;) {
                next = reader.readNext();
                if(next != null) {
                    list.add(next);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> countryNames = new ArrayList<String>();
        ArrayList<String> countryAbber = new ArrayList<String>();
        ArrayList<String> countryCodes = new ArrayList<String>();

        for(int i=0; i < list.size(); i++)
        {
//            countryNames.add(list.get(i)[0]); // gets name
//            countryAbber.add(list.get(i)[1]); // gets abbreviation
            countryCodes.add(list.get(i)[2] + " +" + list.get(i)[1]); // gets calling code

        }


        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this, R.layout.list_row_country_code_spinner, countryCodes);
        countryCodeSpinner.setAdapter(countryAdapter);

// adding event to display codes when country is selected


        countryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long arg3) {
                countrycodeString = "+"+list.get(pos)[1];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


    }

}
