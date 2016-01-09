package com.webmedia7.mohsinhussain.fetchmethere.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.CSVReader;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.RoundedImageView;
import com.webmedia7.mohsinhussain.fetchmethere.FetchMeThere;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegisterActivity extends Activity {

    private TextView headerTextView;
    private Button nextStepButton;
    private int selectedUserPosition;
    Button countryCodeButton;
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
    ArrayList<String> countryCodes;
    ArrayList<String> countryCodeNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        headerTextView = (TextView) findViewById(R.id.header_text_view);
        nextStepButton = (Button) findViewById(R.id.next_step_button);
        countryCodeButton = (Button) findViewById(R.id.country_code_button);

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

        selectedUserString = getIntent().getStringExtra(Constants.CURRENT_USER_TYPE_STRING_INTENT);
        if (selectedUserString.equalsIgnoreCase("p")) {
            headerTextView.setText("REGISTER AS PRIVATE USER");
        } else if (selectedUserString.equalsIgnoreCase("b")) {
            headerTextView.setText("REGISTER AS BUSINESS USER");
        } else if (selectedUserString.equalsIgnoreCase("a")) {
            headerTextView.setText("REGISTER AS AGENT");
        }

        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileNumberString = mobileNumberEditText.getText().toString();
                firstNameString = firstNameEditText.getText().toString();
                lastNameString = lastNameEditText.getText().toString();
                emailString = emailEditText.getText().toString();
                if (mobileNumberString.equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), "Please provide mobile number!", Toast.LENGTH_LONG).show();
                    mobileNumberEditText.setHintTextColor(Color.parseColor("red"));
                    mobileNumberEditText.requestFocus();
                    return;
                }
                else if(firstNameString.equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), "Please provide first name!", Toast.LENGTH_LONG).show();
                    firstNameEditText.setHintTextColor(Color.parseColor("red"));
                    firstNameEditText.requestFocus();
                    return;
                }
                else if(lastNameString.equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), "Please provide last name!", Toast.LENGTH_LONG).show();
                    lastNameEditText.setHintTextColor(Color.parseColor("red"));
                    lastNameEditText.requestFocus();
                    return;
                }
                else if(countryCodeButton.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Please provide country code!", Toast.LENGTH_LONG).show();
                    countryCodeButton.requestFocus();
                    return;
                }
                if (Constants.isNetworkAvailable(RegisterActivity.this)){
//                    Random rn = new Random();
//                    int max = 999999;
//                    int min = 100000;
//                    int verificationCode = rn.nextInt(max - min + 1) + min;
//
//                    final HttpClient httpclient = new DefaultHttpClient();
//
//                    final HttpPost httppost = new HttpPost(
//                            "https://api.twilio.com/2010-04-01/Accounts/"+Constants.ACCOUNT_SID+"/SMS/Messages");
//                    String base64EncodedCredentials = "Basic "
//                            + Base64.encodeToString(
//                            (Constants.ACCOUNT_SID + ":" + Constants.AUTH_TOKEN).getBytes(),
//                            Base64.NO_WRAP);
//
//                    httppost.setHeader("Authorization",
//                            base64EncodedCredentials);
//                    try {
//
//                        String countrycodeString = countryCodeButton.getText().toString().replace(" ", "");
//                        String completeMobileNumber = countrycodeString+mobileNumberString;
//
//                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//                        nameValuePairs.add(new BasicNameValuePair("From",
//                                "+17158022875"));
//                        nameValuePairs.add(new BasicNameValuePair("To",
//                                completeMobileNumber));
//                        nameValuePairs.add(new BasicNameValuePair("Body",
//                                "FetchMeThere - Your code for App registration is "+verificationCode+". Please do not reply to this message."));
//                        nameValuePairs.add(new BasicNameValuePair("Format",
//                                "JSON"));
//
//                        httppost.setEntity(new UrlEncodedFormEntity(
//                                nameValuePairs));
//
//                        // Execute HTTP Post Request
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                HttpResponse response = null;
//                                try {
//                                    response = httpclient.execute(httppost);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                if(response!=null){
//                                    HttpEntity entity = response.getEntity();
//                                    try {
//                                        String responseString = EntityUtils.toString(entity, "UTF-8");
//                                        System.out.println("responseString is: "
//                                                + responseString);
//
//                                        if (responseString.contains("<Status>400</Status>")){
//                                            if(responseString.contains("21608")){
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        Toast.makeText(RegisterActivity.this,"Please Upgrade your account and purchase a twillio number to send message to unverified numbers.",Toast.LENGTH_LONG).show();
//                                                    }
//                                                });
//                                            }
//                                            else if(responseString.contains("21211")){
//
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        Toast.makeText(RegisterActivity.this,"Given number is not a valid phone number",Toast.LENGTH_LONG).show();
//                                                    }
//                                                });
//
//                                            }
//                                            else if(responseString.contains("21408")){
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        Toast.makeText(RegisterActivity.this,"Permission to send an SMS has not been enabled for the region indicated",Toast.LENGTH_LONG).show();
//                                                    }
//                                                });
//
//                                            }
//                                        }
//
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                else{
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(RegisterActivity.this, "We cannot send you code. Please check your internet connection. Thanks", Toast.LENGTH_LONG).show();
//                                        }
//                                    });
//
//                                }
//
//
//
//                            }
//                        }).start();
//
//
//                    }  catch (IOException e) {
//
//                    }



//                /*****
//                 * *** SEND AN API CALL TO GET A VALIDATION CODE *******
//                 * ADD VERIFICATION CODE RECEIVED IN RESPONSE TO INTENTS BELOW
//                 * ****/
//
//                TwilioRestClient client = new TwilioRestClient(Constants.ACCOUNT_SID, Constants.AUTH_TOKEN);
//
//                // Build a filter for the MessageList
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("Body", "Jenny please?! I love you <3"));
//                params.add(new BasicNameValuePair("To", "+971562022892"));
//                params.add(new BasicNameValuePair("From", "+254725834142"));
//                params.add(new BasicNameValuePair("MediaUrl", "http://www.example.com/hearts.png"));
//
//
//                MessageFactory messageFactory = client.getAccount().getMessageFactory();
//                Message message = null;
//                try {
//                    message = messageFactory.create(params);
//                } catch (TwilioRestException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(message.getSid());


                    Intent mobileVerificationIntent = new Intent(RegisterActivity.this, MobileVerificationActivity.class);
                    String countrycodeString = countryCodeButton.getText().toString().replace(" ", "");
                    mobileVerificationIntent.putExtra(Constants.COUNTRY_CODE_STRING_INTENT, countrycodeString);
                    mobileVerificationIntent.putExtra(Constants.MOBILE_NUMBER_STRING_INTENT, mobileNumberString);
                    mobileVerificationIntent.putExtra(Constants.FIRST_NAME_STRING_INTENT, firstNameString);
                    mobileVerificationIntent.putExtra(Constants.LAST_NAME_STRING_INTENT, lastNameString);
                    mobileVerificationIntent.putExtra(Constants.EMAIL_STRING_INTENT, emailString);
                    mobileVerificationIntent.putExtra(Constants.CURRENT_USER_TYPE_STRING_INTENT, selectedUserString);
                    mobileVerificationIntent.putExtra(Constants.PROFILE_IMAGE_STRING_INTENT, profileImageFile);
//                    mobileVerificationIntent.putExtra(Constants.VERIFICATION_CODE_STRING_INTENT, Integer.toString(verificationCode));
                    startActivity(mobileVerificationIntent);
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Please connect to internet.", Toast.LENGTH_LONG).show();
                }

            }
        });


//        String locale = getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry();
//
//        TelephonyManager tm = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
//        final String countryCodeValue = tm.getNetworkCountryIso();

        populateCountryCodes();

        countryCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                        RegisterActivity.this);
//                builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle("Select your country code");
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        RegisterActivity.this,
                        android.R.layout.simple_list_item_1);
                arrayAdapter.addAll(countryCodes);
                builderSingle.setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(arrayAdapter,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strName = arrayAdapter.getItem(which);
                                Log.v("", "Selected Country Code: " + strName);
                                countryCodeButton.setText(countryCodeNumbers.get(which));
//                                AlertDialog.Builder builderInner = new AlertDialog.Builder(
//                                        RegisterActivity.this);
//                                builderInner.setMessage(strName);
//                                builderInner.setTitle("Your Selected Item is");
//                                builderInner.setPositiveButton("Ok",
//                                        new DialogInterface.OnClickListener() {
//
//                                            @Override
//                                            public void onClick(
//                                                    DialogInterface dialog,
//                                                    int which) {
//                                                dialog.dismiss();
//                                            }
//                                        });
//                                builderInner.show();
                            }
                        });
                builderSingle.show();
            }
        });


//        assignValidation();


        for(int i=0; i < list.size(); i++)
        {
            if(list.get(i)[0].contains("Kenya")){
                countryCodeButton.setText(countryCodeNumbers.get(i));
            }
//            if(list.get(i)[1].contains("")){
//                countryCodeButton.setText(countryCodeNumbers.get(i));
//            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Tracker t = FetchMeThere.getInstance().tracker;
        t.setScreenName("Register");
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }


    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        startActivityForResult(intent, REQUEST_CAMERA);

//        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
//        builder.setTitle("Add Photo!");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                if (items[item].equals("Take Photo")) {
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
//                    startActivityForResult(intent, REQUEST_CAMERA);
//                } else if (items[item].equals("Choose from Library")) {
//
//                    Intent intent = new Intent();
//                    // call android default gallery
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    // ******** code for crop image
//                    intent.putExtra("crop", "true");
//                    intent.putExtra("aspectX", 0);
//                    intent.putExtra("aspectY", 0);
//                    intent.putExtra("outputX", 200);
//                    intent.putExtra("outputY", 200);
//
//                    try {
//
//                        intent.putExtra("return-data", true);
//                        startActivityForResult(Intent.createChooser(intent,
//                                "Complete action using"), SELECT_FILE);
//
//                    } catch (ActivityNotFoundException e) {
//                        // Do nothing for now
//                    }
//                } else if (items[item].equals("Cancel")) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
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

    public String getAbsolutePath(Uri uri) {
        System.out.println("URI: " + uri.getAuthority());

        if (uri.getAuthority().contains("media")){
            String[] projection = { MediaStore.MediaColumns.DATA };
            String[] column = { MediaStore.Images.Media.DATA };
            String[] col = {android.provider.MediaStore.Images.ImageColumns.DATA};
            @SuppressWarnings("deprecation")
            Cursor cursor = getContentResolver().query(uri, projection,
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
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
                        Toast.makeText(RegisterActivity.this, "Cannot Use this picture", Toast.LENGTH_LONG).show();
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
        countryCodeNumbers = new ArrayList<String>();
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
        countryCodes = new ArrayList<String>();

        for(int i=0; i < list.size(); i++)
        {
//            countryNames.add(list.get(i)[0]); // gets name
            countryCodeNumbers.add(" +" +list.get(i)[1]); // gets abbreviation
            countryCodes.add(list.get(i)[0] + " +" + list.get(i)[1]); // gets calling code

        }


//        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this, R.layout.list_row_country_code_spinner, countryCodes);
//        countryCodeSpinner.setAdapter(countryAdapter);
//
//// adding event to display codes when country is selected
//
//
//        countryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> arg0, View arg1,
//                                       int pos, long arg3) {
//                countrycodeString = "+"+list.get(pos)[1];
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//                // TODO Auto-generated method stub
//
//            }
//        });


    }

}
