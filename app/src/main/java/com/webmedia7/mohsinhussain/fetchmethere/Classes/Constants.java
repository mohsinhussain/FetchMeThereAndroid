package com.webmedia7.mohsinhussain.fetchmethere.Classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

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
    public static final String BASE_URL = "https://fetchmethere.firebaseio.com/";

    public static final void setImageViewFromString(String imageString, ImageView imageView){
        if(!imageString.equalsIgnoreCase("")){
            byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageView.setImageBitmap(bitmap);
        }

    }

}
