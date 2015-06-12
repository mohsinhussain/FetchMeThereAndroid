package com.webmedia7.mohsinhussain.fetchmethere.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.webmedia7.mohsinhussain.fetchmethere.Model.NavDrawerItem;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.util.ArrayList;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater;
            if (navDrawerItems.get(position).isTitle()){
                mInflater = (LayoutInflater)
                        context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.drawer_profile_list_item, null);


                ImageView profilePicImageView = (ImageView) convertView.findViewById(R.id.profile_image);
                TextView displayNameTextView = (TextView) convertView.findViewById(R.id.displayName);
                TextView chatCounterTextView = (TextView) convertView.findViewById(R.id.chatCounter);
                TextView navCounterTextView = (TextView) convertView.findViewById(R.id.navCounter);
                String profileImageString = navDrawerItems.get(position).getProfilePicUrl();
                if(profileImageString!=null && (!profileImageString.equalsIgnoreCase(""))){
                    profilePicImageView.setVisibility(View.VISIBLE);
                    byte[] byteArray = Base64.decode(profileImageString, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    profilePicImageView.setImageBitmap(bitmap);
                }


//                imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
                displayNameTextView.setText(navDrawerItems.get(position).getDisplayName());
                chatCounterTextView.setText(navDrawerItems.get(position).getChatCount());
                navCounterTextView.setText(navDrawerItems.get(position).getNavCount());
            }
            else{
                mInflater = (LayoutInflater)
                        context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.drawer_list_item, null);


                ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
                TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

                imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
                txtTitle.setText(navDrawerItems.get(position).getTitle());
            }


        }
        // displaying count
        // check whether it set visible or not
//        if(navDrawerItems.get(position).getCounterVisibility()){
//            txtCount.setText(navDrawerItems.get(position).getCount());
//        }else{
//            // hide the counter view
//            txtCount.setVisibility(View.GONE);
//        }

        return convertView;
    }

}
