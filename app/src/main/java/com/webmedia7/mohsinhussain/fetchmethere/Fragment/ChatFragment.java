package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.webmedia7.mohsinhussain.fetchmethere.Adapters.ChatAdapter;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.FetchMeThere;
import com.webmedia7.mohsinhussain.fetchmethere.Model.Chat;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class ChatFragment extends Fragment {

    public ChatFragment(){}

//    private OnFragmentInteractionListener mListener;

    ListView chatListView;
    SharedPreferences preferenceSettings;
    String userId = "";
    ArrayList<Chat> chatArrayList = new ArrayList<Chat>();
    ChatAdapter mAdapter;
    ProgressDialog ringProgressDialog;
    ImageButton sendButton;
    EditText inputMessage;

    String myProfilePicture = "", friendProfilePicture = "", friendName = "", action = "", friendId = "", lastChildKey = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        preferenceSettings = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
        userId = preferenceSettings.getString("userId", null);
        myProfilePicture = preferenceSettings.getString("profileImageString", null);
        Bundle bundle = getArguments();
        if(bundle!=null){
            action = bundle.getString("action");
            friendId = bundle.getString("friendId");
            friendName = bundle.getString("friendName");
            friendProfilePicture = bundle.getString("profileImageString");
        }

        chatListView = (ListView) rootView.findViewById(R.id.chat_list_view);
        sendButton = (ImageButton) rootView.findViewById(R.id.sendButton);
        inputMessage = (EditText) rootView.findViewById(R.id.inputEditText);

        chatArrayList = new ArrayList<Chat>();

        mAdapter = new ChatAdapter(getActivity(), chatArrayList, myProfilePicture, friendProfilePicture, friendName);

        chatListView.setAdapter(mAdapter);


        setHasOptionsMenu(false);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputMessage.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Please write something first!", Toast.LENGTH_SHORT).show();
                } else {
                    // Get a reference to our posts
                    final Firebase ref = new Firebase(Constants.BASE_URL);
                    Firebase postRef = ref.child("users").child(userId).child("chats").child(friendId);
                    Map<String, Object> post1 = new HashMap<String, Object>();
                    post1.put("message", inputMessage.getText().toString());
                    post1.put("isFromFriend", false);
                    postRef.push().setValue(post1);

                    Firebase friendPostRef = ref.child("users").child(friendId).child("chats").child(userId);
                    Map<String, Object> post2 = new HashMap<String, Object>();
                    post2.put("message", inputMessage.getText().toString());
                    post2.put("isFromFriend", true);
                    friendPostRef.push().setValue(post2);

                    inputMessage.setText("");
                }
            }
        });

        inputMessage.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    sendButton.performClick();

                    return false;
                }

                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Tracker t = FetchMeThere.getInstance().tracker;
        t.setScreenName("Chat");
        t.send(new HitBuilders.ScreenViewBuilder().build());
        chatArrayList.clear();
//        ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Loading Chat History...", true);
//        ringProgressDialog.setCancelable(true);

        // Get a reference to our posts
        final Firebase ref = new Firebase(Constants.BASE_URL);
        Firebase postRef = ref.child("users").child(userId).child("chats").child(friendId);

        postRef.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to the database
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                System.out.println(snapshot);

//                chatArrayList.clear();
                Chat chat = new Chat();
                for (DataSnapshot child : snapshot.getChildren()) {
                    lastChildKey = previousChildKey;
                    System.out.println("KEY: " + child.getKey());
                    System.out.println("VALUE: " + child.getValue());
                    if (child.getKey().equalsIgnoreCase("message")) {
                        chat.setMessage(child.getValue().toString());
                    } else if (child.getKey().equalsIgnoreCase("isFromFriend")) {
                        chat.setFromFriend((boolean) child.getValue());
                    }
                }

                chatArrayList.add(chat);

//                ringProgressDialog.dismiss();

                mAdapter.mHighlightedPositions = new boolean[chatArrayList.size()];
                mAdapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                ringProgressDialog.dismiss();
            }

            //... ChildEventListener also defines onChildChanged, onChildRemoved,
            //    onChildMoved and onCanceled, covered in later sections.
        });

    }

    private void scrollMyListViewToBottom() {
        chatListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                chatListView.setSelection(mAdapter.getCount() - 1);
            }
        });
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
//                if (mListener!=null){
//                    mListener.onActionAddLocation();
//                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onActionAddLocation();
//    }
}
