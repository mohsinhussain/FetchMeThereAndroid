package com.webmedia7.mohsinhussain.fetchmethere.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.RoundedImageView;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.util.HashMap;
import java.util.Map;

public class MobileVerificationActivity extends Activity {

    private EditText firstInput;
    private EditText secondInput;
    private EditText thirdInput;
    private Button verifyAccountButton;
    private TextView headerTextView;
    private TextView mobileNumberTextView;
    private String countryCodeString;
    private String mobileNumberString;
    private String firstNameString;
    private String lastNameString;
    private String emailString;
    private String emailAuth;
    private String profileImageString;
    private String selectedUserString;
    private String verificationCode;
    private String enteredVerificationCode = "";
    private String completeMobileNumber = "";
    private String password = "";
    private String Tag = "MobileVerificationActivity";
    private SharedPreferences preferenceSettings;
    private SharedPreferences.Editor preferenceEditor;
    private static final int PREFERENCE_MODE_PRIVATE = 0;
    ImageView headerImageView;
    RoundedImageView profileImageView;
    ProgressDialog ringProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);
        Firebase.setAndroidContext(this);

        firstInput = (EditText) findViewById(R.id.first_two_numbers_edit_text);
        secondInput = (EditText) findViewById(R.id.second_two_numbers_edit_text);
        thirdInput = (EditText) findViewById(R.id.third_two_numbers_edit_text);
        verifyAccountButton = (Button) findViewById(R.id.verify_account_button);
        mobileNumberTextView = (TextView) findViewById(R.id.mobile_number_text_view);
        headerTextView = (TextView) findViewById(R.id.header_text_view);
        headerImageView = (ImageView) findViewById(R.id.header_image_view);
        profileImageView = (RoundedImageView) findViewById(R.id.profile_image_view);


        showMobileNumber();

        populateData();

        if(!profileImageString.equalsIgnoreCase("")){
            headerImageView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
            byte[] byteArray = Base64.decode(profileImageString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            profileImageView.setImageBitmap(bitmap);
        }


        verifyAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredVerificationCode = firstInput.getText().toString()+secondInput.getText().toString()+thirdInput.getText().toString();
                if(verificationCode.equalsIgnoreCase(enteredVerificationCode)){
                    //Do the registeration here!

                    registerUser();

                }
                else{
                    Toast.makeText(getApplicationContext(), "Verification Code Is Invalid!", Toast.LENGTH_LONG).show();
                }

            }
        });


        firstInput.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (firstInput.getText().toString().length() == 2)     //size as per your requirement
                {
                    secondInput.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        secondInput.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (secondInput.getText().toString().length() == 2)     //size as per your requirement
                {
                    thirdInput.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        thirdInput.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (thirdInput.getText().toString().length() == 2)     //size as per your requirement
                {
                    verifyAccountButton.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
    }

    private void loggingUser(){
        ringProgressDialog = ProgressDialog.show(MobileVerificationActivity.this, "Please wait ...", "Signing In User ...", true);
        ringProgressDialog.setCancelable(true);
        final Firebase ref = new Firebase(Constants.BASE_URL);
        Log.v("asda", "Password before logging in: "+password);
        ref.authWithPassword(emailAuth, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Log.v(Tag, "Logging AuthData: "+authData.toString());
                Log.v(Tag, "Logging AuthData.getUid(): "+authData.getUid().toString());
                Log.v(Tag, "Logging AuthData.getAuth(): "+authData.getAuth().toString());
                Log.v(Tag, "Logging AuthData.getExpires(): "+authData.getExpires());
                Log.v(Tag, "Logging AuthData.getProvider(): "+authData.getProvider());
                Log.v(Tag, "Logging AuthData.getToken(): "+authData.getToken());
                Log.v(Tag, "Logging AuthData.getUid(): "+authData.getProviderData());

                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());

                // Authentication just completed successfully :)
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("provider", authData.getProvider());
                if(authData.getProviderData().containsKey("id")) {
                    map.put("provider_id", authData.getProviderData().get("id").toString());
                }
                if(!emailString.equalsIgnoreCase("")){
                    map.put("email", emailString);
                }

                map.put("displayName", firstNameString);
                map.put("firstName", firstNameString);
                map.put("lastName", lastNameString);
                map.put("mobileNumber", completeMobileNumber);
                map.put("profileImageString", profileImageString);
                if(selectedUserString.equalsIgnoreCase("p")){
                   map.put("private_registered", "true");
                }
                else if(selectedUserString.equalsIgnoreCase("b")){
                    map.put("business_registered", "true");
                    map.put("private_registered", "true");
                }
                else if(selectedUserString.equalsIgnoreCase("a")){
                    map.put("agent_registered", "true");
                    map.put("business_registered", "true");
                    map.put("private_registered", "true");
                }
                ref.child("users").child(authData.getUid()).updateChildren(map);
//                ref.child("users").child(authData.getProviderData().get("email").toString()).setValue(map);


                preferenceSettings = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
                preferenceEditor = preferenceSettings.edit();
                preferenceEditor.putString("userId", authData.getUid());
                preferenceEditor.putString("email", emailString);
                preferenceEditor.putString("firstName", firstNameString);
                preferenceEditor.putString("lastName", lastNameString);
                preferenceEditor.putString("displayName", firstNameString);
                preferenceEditor.putString("mobileNumber", completeMobileNumber);
                preferenceEditor.putString("selectedUserString", selectedUserString);
                preferenceEditor.putString("profileImageString", profileImageString);
                preferenceEditor.commit();
                ringProgressDialog.dismiss();

                Intent mainIntent = new Intent(getApplicationContext(), MainPrivateActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);


                //Start Private Main Activity

            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
                ringProgressDialog.dismiss();
                switch (firebaseError.getCode()) {
                    case FirebaseError.USER_DOES_NOT_EXIST:
                        // handle a non existing user
                        Log.v(Tag, "Register Error Code: USER_DOES_NOT_EXIST");
                        Toast.makeText(getApplicationContext(), "NETWORK_ERROR", Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.INVALID_PASSWORD:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: INVALID_PASSWORD");
                        Toast.makeText(getApplicationContext(), "NETWORK_ERROR", Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.AUTHENTICATION_PROVIDER_DISABLED:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: AUTHENTICATION_PROVIDER_DISABLED");
                        Toast.makeText(getApplicationContext(), "NETWORK_ERROR", Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.EMAIL_TAKEN:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: EMAIL_TAKEN");
                        Toast.makeText(getApplicationContext(), "EMAIL_TAKEN", Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.INVALID_AUTH_ARGUMENTS:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: INVALID_AUTH_ARGUMENTS");
                        Toast.makeText(getApplicationContext(), "INVALID_AUTH_ARGUMENTS", Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.INVALID_CONFIGURATION:
                        // handle an invalid password
                        Log.v(Tag, "IRegister Error Code: INVALID_CONFIGURATION");
                        Toast.makeText(getApplicationContext(), "INVALID_CONFIGURATION", Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.INVALID_CREDENTIALS:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: INVALID_CREDENTIALS");
                        Toast.makeText(getApplicationContext(), "INVALID_CREDENTIALS", Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.INVALID_EMAIL:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: INVALID_EMAIL");
                        Toast.makeText(getApplicationContext(), "INVALID_EMAIL", Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.INVALID_TOKEN:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: INVALID_TOKEN");
                        Toast.makeText(getApplicationContext(), "INVALID_TOKEN", Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.NETWORK_ERROR:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: NETWORK_ERROR");
                        Toast.makeText(getApplicationContext(), "NETWORK_ERROR", Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.PREEMPTED:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: PREEMPTED");
                        Toast.makeText(getApplicationContext(), "PREEMPTED", Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.PROVIDER_ERROR:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: PROVIDER_ERROR");
                        Toast.makeText(getApplicationContext(), "PROVIDER_ERROR", Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.UNKNOWN_ERROR:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: UNKNOWN_ERROR");
                        Toast.makeText(getApplicationContext(), "UNKNOWN_ERROR", Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.DENIED_BY_USER:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: DENIED_BY_USER");
                        Toast.makeText(getApplicationContext(), "DENIED_BY_USER", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        // handle other errors
                        Log.v(Tag, "Register Error Code: DEFAULT ERROR");
                        Toast.makeText(getApplicationContext(), "DEFAULT ERROR", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void registerUser(){
        ringProgressDialog = ProgressDialog.show(MobileVerificationActivity.this, "Please wait ...", "Signing up User ...", true);
        ringProgressDialog.setCancelable(true);

        Firebase ref = new Firebase(Constants.BASE_URL);
        ref.createUser(emailAuth, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                Log.v(Tag, "Registeration Response: " + result.toString());
                System.out.println("Successfully created user account with uid: " + result.get("uid"));
                //Login User here
                ringProgressDialog.dismiss();
                loggingUser();

            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // there was an error
                ringProgressDialog.dismiss();
                switch (firebaseError.getCode()) {
                    case FirebaseError.USER_DOES_NOT_EXIST:
                        // handle a non existing user
                        Log.v(Tag, "Register Error Code: USER_DOES_NOT_EXIST");
                        break;
                    case FirebaseError.INVALID_PASSWORD:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: INVALID_PASSWORD");
                        break;
                    case FirebaseError.AUTHENTICATION_PROVIDER_DISABLED:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: AUTHENTICATION_PROVIDER_DISABLED");
                        break;
                    case FirebaseError.EMAIL_TAKEN:
                        // handle an invalid password
                        loggingUser();
                        Log.v(Tag, "Register Error Code: EMAIL_TAKEN");
                        break;
                    case FirebaseError.INVALID_AUTH_ARGUMENTS:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: INVALID_AUTH_ARGUMENTS");
                        break;
                    case FirebaseError.INVALID_CONFIGURATION:
                        // handle an invalid password
                        Log.v(Tag, "IRegister Error Code: NVALID_CONFIGURATION");
                        break;
                    case FirebaseError.INVALID_CREDENTIALS:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: INVALID_CREDENTIALS");
                        break;
                    case FirebaseError.INVALID_EMAIL:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: INVALID_EMAIL");
                        break;
                    case FirebaseError.INVALID_TOKEN:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: INVALID_TOKEN");
                        break;
                    case FirebaseError.NETWORK_ERROR:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: NETWORK_ERROR");
                        break;
                    case FirebaseError.PREEMPTED:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: PREEMPTED");
                        break;
                    case FirebaseError.PROVIDER_ERROR:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: PROVIDER_ERROR");
                        break;
                    case FirebaseError.UNKNOWN_ERROR:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: UNKNOWN_ERROR");
                        break;
                    case FirebaseError.DENIED_BY_USER:
                        // handle an invalid password
                        Log.v(Tag, "Register Error Code: DENIED_BY_USER");
                        break;
                    default:
                        // handle other errors
                        Log.v(Tag, "Register Error Code: DEFAULT ERROR");
                        break;
                }
            }
        });
    }

    private void showMobileNumber (){

        countryCodeString = getIntent().getStringExtra(Constants.COUNTRY_CODE_STRING_INTENT);
        mobileNumberString = getIntent().getStringExtra(Constants.MOBILE_NUMBER_STRING_INTENT);
        mobileNumberTextView.setText(countryCodeString+" (0) "+mobileNumberString);
    }

    private void populateData(){
        completeMobileNumber = countryCodeString+mobileNumberString;
        password = completeMobileNumber;
        firstNameString = getIntent().getStringExtra(Constants.FIRST_NAME_STRING_INTENT);
        lastNameString = getIntent().getStringExtra(Constants.LAST_NAME_STRING_INTENT);
        emailString = getIntent().getStringExtra(Constants.EMAIL_STRING_INTENT);
        profileImageString = getIntent().getStringExtra(Constants.PROFILE_IMAGE_STRING_INTENT);
        verificationCode = getIntent().getStringExtra(Constants.VERIFICATION_CODE_STRING_INTENT);
        emailAuth = completeMobileNumber+"@fetchmethere.com";
        selectedUserString = getIntent().getStringExtra(Constants.CURRENT_USER_TYPE_STRING_INTENT);
        if(selectedUserString.equalsIgnoreCase("p")){
            headerTextView.setText("REGISTER AS PRIVATE USER");
        }
        else if(selectedUserString.equalsIgnoreCase("b")){
            headerTextView.setText("REGISTER AS BUSINESS USER");
        }
        else if(selectedUserString.equalsIgnoreCase("a")){
            headerTextView.setText("REGISTER AS AGENT");
        }
    }


}
