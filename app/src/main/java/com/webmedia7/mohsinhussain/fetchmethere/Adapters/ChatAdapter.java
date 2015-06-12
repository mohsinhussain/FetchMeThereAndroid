package com.webmedia7.mohsinhussain.fetchmethere.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.RoundedImageView;
import com.webmedia7.mohsinhussain.fetchmethere.Model.Chat;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.util.ArrayList;

/**
 * Created by mohsinhussain on 3/22/15.
 */
public class ChatAdapter extends BaseAdapter {

    /************** Declare used variables ******************/
    private Activity activity;
    private ArrayList<Chat> data;
    private static LayoutInflater inflater = null;
    Chat tempValues = null;
    int i = 0;
    public boolean[] mHighlightedPositions;
    String myProfilePicture,  friendProfilePicture,  friendName;
    private static final int ITEM_VIEW_TYPE_MINE = 0;
    private static final int ITEM_VIEW_TYPE_FRIEND = 1;


    /*************  CustomAdapter Constructor *****************/
    public ChatAdapter(Activity a, ArrayList<Chat> d, String myProfilePicture, String friendProfilePicture, String friendName) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        mHighlightedPositions = new boolean[d.size()];
        this.myProfilePicture = myProfilePicture;
        this.friendProfilePicture = friendProfilePicture;
        this.friendName = friendName;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }



    @Override
    public int getCount() {
//        if(data.size()<=0)
//            return 1;
        return data.size();
    }

    @Override
    public Chat getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /** THIS SOLVED SCROLLING ISSUE WITH FALSE VALUES **/
    @Override
    public int getItemViewType(int position) {
        return this.getItem(position).isFromFriend() ? ITEM_VIEW_TYPE_FRIEND : ITEM_VIEW_TYPE_MINE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView messageTextView;
        public RoundedImageView profileImageView;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****************** Inflate list_row.xml for each row ******************/
            vi =  inflater.inflate(data.get(position).isFromFriend() ? R.layout.list_row_friend_chat : R.layout.list_row_my_chat, null);
//        if(data.get(position).isFromFriend()){
//            vi = inflater.inflate(R.layout.list_row_friend_chat, null);
//        }
//        else{
//            vi = inflater.inflate(R.layout.list_row_my_chat, null);
//        }


            /****************** View Holder Object to contain list_row.xml file elements *******************/
            holder = new ViewHolder();
            holder.messageTextView = (TextView) vi.findViewById(R.id.message_text_view);
            holder.profileImageView = (RoundedImageView) vi.findViewById(R.id.profile_image_view);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        }
        else{
            holder=(ViewHolder)vi.getTag();
        }

        if(data.size()<=0){
//            holder.messageTextView.setText("Please start conversation with your first message");
//            holder.profileImageView.setVisibility(View.GONE);
        }
        else{
            /****************** Get Each Model Object From Array List *******************/
            tempValues = null;
            tempValues = (Chat) data.get(position);

            /****************** Set Model Values in Holder Elements *****************/
            holder.messageTextView.setText(tempValues.getMessage());
//            holder.descriptionTextView.setText(tempValues.getDescription());
//            holder.iconImageView.setBackground(activity.getResources().getDrawable(tempValues.getIconImage()));
//            holder.profileImageView.setTag(position);

            Constants.setImageViewFromString(tempValues.isFromFriend() ? friendProfilePicture : myProfilePicture, holder.profileImageView);

//            if(tempValues.isFromFriend()){
//                Constants.setImageViewFromString(friendProfilePicture, holder.profileImageView);
//            }
//            else{
//                Constants.setImageViewFromString(myProfilePicture, holder.profileImageView);
//            }

        }



        return vi;
    }
}
