package com.webmedia7.mohsinhussain.fetchmethere.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.RoundedImageView;
import com.webmedia7.mohsinhussain.fetchmethere.Fragment.ContactsFragment;
import com.webmedia7.mohsinhussain.fetchmethere.Model.Contacts;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.util.ArrayList;

/**
 * Created by mohsinhussain on 3/22/15.
 */
public class ContactsAdapter extends BaseAdapter implements View.OnClickListener {

    /************** Declare used variables ******************/
    private Activity activity;
    private ArrayList<Contacts> data;
    private static LayoutInflater inflater = null;
    Contacts tempValues = null;
    int i = 0;
    public boolean[] mHighlightedPositions;
    String action = "";
    ContactsFragment parentFragment;
    private static final int ITEM_VIEW_TYPE_MINE = 0;
    private static final int ITEM_VIEW_TYPE_FRIEND = 1;


    /*************  CustomAdapter Constructor *****************/
    public ContactsAdapter(Activity a, ArrayList<Contacts> d, String action, ContactsFragment parentFragment) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        mHighlightedPositions = new boolean[d.size()];
        this.action = action;
        this.parentFragment = parentFragment;

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
    public Contacts getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /** THIS SOLVED SCROLLING ISSUE WITH FALSE VALUES **/
    @Override
    public int getItemViewType(int position) {
        return this.getItem(position).getProfileImageString().equalsIgnoreCase("") ? ITEM_VIEW_TYPE_FRIEND : ITEM_VIEW_TYPE_MINE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }



    /********* Create a holder Class to contain inflated xml file elements *********/
    private static class ViewHolder{

        public TextView nameTextView;
        public TextView optionTextView;
        public ImageView iconImageView;
        public LinearLayout optionLayout;
        public RoundedImageView profileImageView;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****************** Inflate list_row.xml for each row ******************/
            vi = inflater.inflate(R.layout.list_row_contacts, null);


            /****************** View Holder Object to contain list_row.xml file elements *******************/
            holder = new ViewHolder();
            holder.nameTextView = (TextView) vi.findViewById(R.id.name_text_view);
            holder.optionTextView = (TextView) vi.findViewById(R.id.option_text_view);
            holder.iconImageView = (ImageView) vi.findViewById(R.id.icon_image_view);
            holder.optionLayout = (LinearLayout) vi.findViewById(R.id.option_button_layout);
            holder.profileImageView = (RoundedImageView) vi.findViewById(R.id.profile_image_view);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        if(data.size()<=0){
            holder.nameTextView.setText("Please Add Contacts.");
            holder.profileImageView.setVisibility(View.GONE);
        }
        else{
            /****************** Get Each Model Object From Array List *******************/
            tempValues = null;
            tempValues = (Contacts) data.get(position);

            /****************** Set Model Values in Holder Elements *****************/
            holder.nameTextView.setText(tempValues.getName());
//            holder.descriptionTextView.setText(tempValues.getDescription());
//            holder.iconImageView.setBackground(activity.getResources().getDrawable(tempValues.getIconImage()));
            holder.optionLayout.setTag(position);
//            holder.profileImageView.setTag(tempValues.getProfileImageString());
//            holder.profileImageView.setTag(tempValues.getProfileImageString());

//            this.getItem(position).getProfileImageString().equalsIgnoreCase("") ? ITEM_VIEW_TYPE_FRIEND : ITEM_VIEW_TYPE_MINE;

            Constants.setImageViewFromString(tempValues.getProfileImageString().equalsIgnoreCase("") ? "" : tempValues.getProfileImageString(), holder.profileImageView);

//            if(!((String)holder.profileImageView.getTag()).equalsIgnoreCase("")){
//                Constants.setImageViewFromString(tempValues.getProfileImageString(), holder.profileImageView);
//            }

            if (data.get(position).isFriend()){
                if (action.equalsIgnoreCase("requestLocation")){
                    holder.optionTextView.setText("REQUEST");
                }
                else if (action.equalsIgnoreCase("sendLocation")){
                    holder.optionTextView.setText("SEND");
                }
                else if (action.equalsIgnoreCase("sendSavedLocation")){
                    holder.optionTextView.setText("SEND");
                }
                else if (action.equalsIgnoreCase("chat")){
                    holder.iconImageView.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.nav_chat_black_icon_bg));
                    holder.optionTextView.setText("CHAT");
                }
            }
            else{
                holder.iconImageView.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.private_user_icon));
                holder.optionTextView.setText("Add Friend");
            }

            /***************** Set Item Click Listner for LayoutInflator for each row ******************/
            vi.setOnClickListener(new OnItemClickListener(position));

            if(mHighlightedPositions[position]) {
                holder.optionLayout.setVisibility(View.VISIBLE);
            }else {
                holder.optionLayout.setVisibility(View.GONE);
            }

        }



        return vi;
    }



    @Override
    public void onClick(View v) {
        Log.v("UserOptionAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {

            LinearLayout optionButton = (LinearLayout)arg0.findViewById(R.id.option_button_layout);

//            if(mHighlightedPositions[mPosition]) {
//                optionButton.setVisibility(View.INVISIBLE);
//                mHighlightedPositions[mPosition] = false;
//            }else {
//                optionButton.setVisibility(View.VISIBLE);
//                mHighlightedPositions[mPosition] = true;
//            }

            for(int i=0; i<mHighlightedPositions.length;i++){
                if(i==mPosition){
                    if(mHighlightedPositions[i]) {
                        optionButton.setVisibility(View.GONE);
                        mHighlightedPositions[i] = false;
                    }else {
                        optionButton.setVisibility(View.VISIBLE);
                        optionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (data.get(mPosition).isFriend()){
                                    if (action.equalsIgnoreCase("requestLocation")){
                                        parentFragment.requestLocation(mPosition);
                                    }
                                    else if (action.equalsIgnoreCase("sendLocation")){
                                        parentFragment.sendLocation(mPosition);
                                    } else if (action.equalsIgnoreCase("sendSavedLocation")){
                                        parentFragment.sendSavedLocation(mPosition);
                                    }
                                    else if (action.equalsIgnoreCase("chat")){
                                        parentFragment.chat(mPosition);
                                    }
                                }
                                else{
                                    //Implement Add Friend
                                    parentFragment.addFriend(mPosition);

                                }

//                                MainPrivateActivity act = (MainPrivateActivity)activity;
//                                Contacts loc = data.get(mPosition);
//                                act.onSeeMore(loc.getName(), loc.getAddress(), loc.getLatitude(), loc.getLongitude(), loc.getImagesArray(), loc.getRefId());

                            }
                        });
                        mHighlightedPositions[i] = true;
                    }
                }
                else{
                    mHighlightedPositions[i] = false;
                }
            }

            notifyDataSetChanged();






//            ChooseUserActivity act = (ChooseUserActivity)activity;
//
//            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/
//
//            act.onItemClick(mPosition);
        }
    }
}
