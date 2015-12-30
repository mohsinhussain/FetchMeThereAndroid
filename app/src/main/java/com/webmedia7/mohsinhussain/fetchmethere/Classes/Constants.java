package com.webmedia7.mohsinhussain.fetchmethere.Classes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohsinhussain on 4/8/15.
 */
public class Constants {
    public static final String COUNTRY_CODE_STRING_INTENT = "countryCodeString";
    public static final String MOBILE_NUMBER_STRING_INTENT = "mobileNumberString";
    public static final String FIRST_NAME_STRING_INTENT = "firstNameString";
    public static final String LAST_NAME_STRING_INTENT = "lastNameString";
    public static final String EMAIL_STRING_INTENT = "emailString";
    public static final String PROFILE_IMAGE_STRING_INTENT = "profileImageString";
    public static final String VERIFICATION_CODE_STRING_INTENT = "verificationCode";
    public static final String CURRENT_USER_TYPE_STRING_INTENT = "currentUserType";
    public static final String MY_PREFS_NAME = "fetchMePref";
    public static final String DB_NAME = "FetchMeThereDataBase";
    public static final int DB_VERSION = 1;
    public static final String BASE_URL = "https://fetchmethere.firebaseio.com/";
    public static final String ACCOUNT_SID = "AC6dfe9d8e3a7641a2d2bcf9f53fa6137d";
    public static final String AUTH_TOKEN = "a24d2eee251d677acecc6dc8d731f99d";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GCM_TOKEN = "gcmToken";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    public static final void setImageViewFromString(String imageString, ImageView imageView){
        if(!imageString.equalsIgnoreCase("")){
            byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageView.setImageBitmap(bitmap);
        }

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static void sendPushNotification(Activity activity, String mobileNumber, String message){
        final Activity act = activity;
        if (Constants.isNetworkAvailable(activity)){

            final HttpClient httpclient = new DefaultHttpClient();

            final HttpPost httppost = new HttpPost(
                    "http://clonify.contrivesol.com/send_message.php");
            try {


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("mobileNumber",
                        mobileNumber));
                nameValuePairs.add(new BasicNameValuePair("message",
                        message));

                httppost.setEntity(new UrlEncodedFormEntity(
                        nameValuePairs));

                // Execute HTTP Post Request
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpResponse response = null;
                        try {
                            response = httpclient.execute(httppost);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(response!=null){
                            HttpEntity entity = response.getEntity();
                            try {
                                String responseString = EntityUtils.toString(entity, "UTF-8");
                                System.out.println("responseString is: "
                                        + responseString);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(act, "Please check your internet connection. Thanks", Toast.LENGTH_LONG).show();
                                }
                            });

                        }



                    }
                }).start();


            }  catch (IOException e) {

            }
        }
        else{
            Toast.makeText(act, "Please connect to internet.", Toast.LENGTH_LONG).show();
        }
    }

}
