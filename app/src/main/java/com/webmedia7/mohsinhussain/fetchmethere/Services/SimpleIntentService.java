package com.webmedia7.mohsinhussain.fetchmethere.Services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.DatabaseHandler;
import com.webmedia7.mohsinhussain.fetchmethere.Model.Contacts;
import com.webmedia7.mohsinhussain.fetchmethere.Model.OnlineReferenceFriends;

import java.util.ArrayList;

/**
 * Created by mohsinhussain on 6/23/15.
 */
public class SimpleIntentService extends IntentService {
    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";
    public Context context;
    ArrayList<OnlineReferenceFriends> olf = new ArrayList<OnlineReferenceFriends>();
    ArrayList<Contacts> contactsArrayList = new ArrayList<Contacts>();
    DatabaseHandler dbHandler;
    public SimpleIntentService() {
        super("SimpleIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Firebase.setAndroidContext(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        dbHandler = new DatabaseHandler(context);
        String msg = intent.getStringExtra(PARAM_IN_MSG);
//            ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Finding Friends...", true);
//            ringProgressDialog.setCancelable(true);

        // Get a reference to our posts
        final Firebase ref = new Firebase(Constants.BASE_URL);
        Firebase postRef = ref.child("users").child(msg).child("friends");


        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot);

                olf.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    OnlineReferenceFriends ol = new OnlineReferenceFriends();
                    for (DataSnapshot mChild : child.getChildren()) {
                        if (mChild.getKey().equalsIgnoreCase("userId")) {
                            ol.setUserId(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("name")) {
                            ol.setName(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("mobile")) {
                            ol.setMobile(mChild.getValue().toString());
                        } else if (mChild.getKey().equalsIgnoreCase("profileImageString")) {
                            ol.setProfileImageString(mChild.getValue().toString());
                        }
                    }
                    olf.add(ol);

                }


                fetchContacts();

//                    Collections.sort(contactsArrayList, new CustomComparator());
//
//                    mAdapter.mHighlightedPositions = new boolean[contactsArrayList.size()];
//                    mAdapter.notifyDataSetChanged();
//                    ringProgressDialog.dismiss();
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.mamlambo.intent.action.MESSAGE_PROCESSED");
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                sendBroadcast(broadcastIntent);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        // processing done here….

    }


    public void fetchContacts() {

        contactsArrayList.clear();
        dbHandler.removeAllContacts();



        String phoneNumber = null;
        String email = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = null;
        if(context!=null){
            contentResolver = context.getContentResolver();



            Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, DISPLAY_NAME + " ASC");

            // Loop for every contact in the phone
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {


                    String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                    String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                    if (hasPhoneNumber > 0) {



                        // Query and loop for every phone number of the contact
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

                        while (phoneCursor.moveToNext()) {
                            Contacts contact = new Contacts();
                            contact.setName(name);
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)).replace(" ", "");
                            phoneNumber = phoneNumber.replace("-", "");
                            contact.setMobileNumber(phoneNumber);
                            contact.setSortFriend("z");

                            for (int i = 0; i<olf.size();i++){
                                if(contact.getMobileNumber().equalsIgnoreCase(olf.get(i).getMobile())){
                                    contact.setMobileNumber(olf.get(i).getMobile());
                                    contact.setUserId(olf.get(i).getUserId());
                                    contact.setFriend(true);
                                    contact.setName(olf.get(i).getName());
                                    contact.setProfileImageString(olf.get(i).getProfileImageString());
                                    contact.setSortFriend("a");
                                }
                            }

                            dbHandler.addContact(contact);
                            contactsArrayList.add(contact);
                        }

                        phoneCursor.close();


                    }
                }
            }
        }
    }
}
