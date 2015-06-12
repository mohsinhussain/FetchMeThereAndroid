package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.webmedia7.mohsinhussain.fetchmethere.Adapters.ContactsAdapter;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Model.Contacts;
import com.webmedia7.mohsinhussain.fetchmethere.Model.OnlineReferenceFriends;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class ContactsFragment extends Fragment {

    public ContactsFragment(){}

    private OnFragmentInteractionListener mListener;

    ListView locationsListView;
    SharedPreferences preferenceSettings;
    String userId = "";
    String displayName = "";
    String mobileNumber = "";
    String profileImageString = "";
    ArrayList<Contacts> contactsArrayList = new ArrayList<Contacts>();
    ArrayList<OnlineReferenceFriends> olf = new ArrayList<OnlineReferenceFriends>();
    ContactsAdapter mAdapter;
    String action = "";
    ProgressDialog ringProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
        userId = preferenceSettings.getString("userId", null);
        displayName = preferenceSettings.getString("displayName", null);
        mobileNumber = preferenceSettings.getString("mobileNumber", null);
        profileImageString = preferenceSettings.getString("profileImageString", null);

        locationsListView = (ListView) rootView.findViewById(R.id.location_list_view);

        Bundle bundle = getArguments();
        if(bundle!=null){
            action = bundle.getString("action");
        }

        mAdapter = new ContactsAdapter(getActivity(), contactsArrayList, action, this);

        locationsListView.setAdapter(mAdapter);

        return rootView;
    }

    public void fetchContacts() {

        contactsArrayList.clear();



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
        if(getActivity()!=null){
            contentResolver = getActivity().getContentResolver();



        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, DISPLAY_NAME + " ASC");

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                Contacts contact = new Contacts();

                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                if (hasPhoneNumber > 0) {

                    contact.setName(name);

                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)).replace(" ", "");
                        phoneNumber = phoneNumber.replace("-", "");
                        contact.setMobileNumber(phoneNumber);
                        contact.setSortFriend("z");

                    }

                    phoneCursor.close();

                    for (int i = 0; i<olf.size();i++){
                        if(contact.getMobileNumber().equalsIgnoreCase(olf.get(i).getMobile())){
                            contact.setUserId(olf.get(i).getUserId());
                            contact.setFriend(true);
                            contact.setName(olf.get(i).getName());
                            contact.setProfileImageString(olf.get(i).getProfileImageString());
                            contact.setSortFriend("a");
                        }
                    }

                    contactsArrayList.add(contact);
                }
            }
        }
        }

        Collections.sort(contactsArrayList, new CustomComparator());

//        Collections.sort(contactsArrayList, new Comparator<Contacts>() {
//            public int compare(Contacts contact, Contacts another) {
//                return contact.getSortFriend().compareToIgnoreCase(another.getSortFriend());
//            }
//        });
    }

    public class CustomComparator implements Comparator<Contacts> {
        @Override
        public int compare(Contacts o1, Contacts o2) {
            return o1.getSortFriend().compareToIgnoreCase(o2.getSortFriend());
        }
    }

    public void sendLocation(int position){
        Contacts friendToSendLocation = contactsArrayList.get(position);
        mListener.onSendLocationToFriend(friendToSendLocation.getUserId(), friendToSendLocation.getName(), friendToSendLocation.getProfileImageString(), getArguments().getDouble("lat"), getArguments().getDouble("lang"));
    }

    public void chat(int position){
        Contacts friendToSendLocation = contactsArrayList.get(position);
        mListener.onChatToFriend(friendToSendLocation.getUserId(), friendToSendLocation.getName(), friendToSendLocation.getProfileImageString());
    }

    public void sendSavedLocation(int position){
        final Contacts friendToSendLocation = contactsArrayList.get(position);
        ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Sending Location...", true);
        ringProgressDialog.setCancelable(true);

        final Firebase ref = new Firebase(Constants.BASE_URL);
        Firebase postRef = ref.child("users").child(friendToSendLocation.getUserId()).child("receivedLocations");
        final Bundle bundle = this.getArguments();
        Map<String, String> post1 = new HashMap<String, String>();
        post1.put("friendId", userId);
        post1.put("friendName", displayName);
        post1.put("locName", bundle.getString("locName"));
        post1.put("address", bundle.getString("address"));
        post1.put("lat", bundle.getString("lat"));
        post1.put("lang", bundle.getString("lang"));
        post1.put("profileImageString", friendToSendLocation.getProfileImageString());
        post1.put("comment", bundle.getString("locName"));

        postRef.push().setValue(post1, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                ringProgressDialog.dismiss();
                if (firebaseError != null) {
                    Toast.makeText(getActivity().getApplicationContext(), "Location could not be sent. " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                } else {

                    System.out.println("Data saved successfully.");
                    if (mListener != null) {
                        mListener.onLocationSent(friendToSendLocation.getName(), bundle.getString("locName"));
                        getFragmentManager().popBackStack();
                        getFragmentManager().popBackStack();
                        getFragmentManager().popBackStack();
                    }
                }
            }
        });
    }

    public void requestLocation(int position){
        Contacts friendToSendLocation = contactsArrayList.get(position);
        mListener.onRequestLocation(friendToSendLocation.getUserId(), friendToSendLocation.getName(), friendToSendLocation.getProfileImageString());
    }

    public void addFriend(int position){

        Contacts friendToBeAdded = contactsArrayList.get(position);
        ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Adding Friend...", true);
        ringProgressDialog.setCancelable(true);
        final Firebase ref = new Firebase(Constants.BASE_URL);
        Firebase postRef = ref.child("users");
        Log.v("ada", "locale mobile number: "+friendToBeAdded.getMobileNumber());
        Query queryRef = postRef.orderByChild("mobileNumber").equalTo(friendToBeAdded.getMobileNumber());

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("Snapshot: " + snapshot.getKey());
                Log.v("AASDas", "Snapshot: " + snapshot.getChildren());
                if (snapshot.getValue()==null){
                    Toast.makeText(getActivity().getApplicationContext(), "USER DOES NOT HAVE FETCHMETHERE INSTALLED", Toast.LENGTH_LONG).show();
                    ringProgressDialog.dismiss();
                }
                else{

                    String uId = "";
                    for (DataSnapshot child : snapshot.getChildren()) {
                        System.out.println("KEY: " + child.getKey());
                        System.out.println("VALUE: " + child.getValue());
                        uId = child.getKey();
                    }
                    final Firebase ref = new Firebase(Constants.BASE_URL);
                    Firebase postRef = ref.child("users").child(uId).child("pendingFriends");

                    Map<String, Object> post2 = new HashMap<String, Object>();
                    post2.put("name", displayName);
                    post2.put("mobileNumber", mobileNumber);
                    post2.put("profileImageString", profileImageString);


                    Map<String, Object> post1 = new HashMap<String, Object>();
                    post1.put(userId, post2);

                    postRef.updateChildren(post1, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            ringProgressDialog.dismiss();
                            if (firebaseError != null) {
                                Toast.makeText(getActivity(), "Add Friend request could not be sent. " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                getFragmentManager().popBackStack();
                                Toast.makeText(getActivity(), "Add Friend request successfully sent. ", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }


        });

    }


    @Override
    public void onResume() {
        super.onResume();

        ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Finding Friends...", true);
        ringProgressDialog.setCancelable(true);

        // Get a reference to our posts
        final Firebase ref = new Firebase(Constants.BASE_URL);
        Firebase postRef = ref.child("users").child(userId).child("friends");


        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot);

                olf.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    OnlineReferenceFriends ol = new OnlineReferenceFriends();
                    for(DataSnapshot mChild : child.getChildren()){
                        if(mChild.getKey().equalsIgnoreCase("userId")){
                            ol.setUserId(mChild.getValue().toString());
                        }
                        else if(mChild.getKey().equalsIgnoreCase("name")){
                            ol.setName(mChild.getValue().toString());
                        }
                        else if(mChild.getKey().equalsIgnoreCase("mobile")){
                            ol.setMobile(mChild.getValue().toString());
                        }
                        else if(mChild.getKey().equalsIgnoreCase("profileImageString")){
                            ol.setProfileImageString(mChild.getValue().toString());
                        }
                    }
                    olf.add(ol);

                }

                fetchContacts();



                mAdapter.mHighlightedPositions = new boolean[contactsArrayList.size()];
                mAdapter.notifyDataSetChanged();
                ringProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
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



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu.hasVisibleItems()){
            super.onCreateOptionsMenu(menu, inflater);
        }
        else{
            inflater.inflate(R.menu.menu_mylocations_fragment, menu);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_location:
            {
                if (mListener!=null){
                    mListener.onActionAddLocation();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onActionAddLocation();
        public void onSendLocationToFriend(String friendId, String friendName, String friendProfileImageString, double lat, double lang);
        public void onChatToFriend(String friendId, String friendName, String friendProfileImageString);
        public void onRequestLocation(String friendId, String friendName, String profileImageView);
        public void onLocationSent(String friendName, String locationName);
    }
}
