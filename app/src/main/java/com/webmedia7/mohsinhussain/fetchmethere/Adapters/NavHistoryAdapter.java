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

import com.webmedia7.mohsinhussain.fetchmethere.Activities.MainPrivateActivity;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.RoundedImageView;
import com.webmedia7.mohsinhussain.fetchmethere.Model.NavigationHistory;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by mohsinhussain on 3/22/15.
 */
public class NavHistoryAdapter extends BaseAdapter implements View.OnClickListener {

    /************** Declare used variables ******************/
    private Activity activity;
    private ArrayList<NavigationHistory> data;
    private static LayoutInflater inflater = null;
    NavigationHistory tempValues = null;
    int i = 0;
    public boolean[] mHighlightedPositions;
    private static final int ITEM_VIEW_TYPE_MINE = 0;
    private static final int ITEM_VIEW_TYPE_FRIEND = 1;

    /*************  CustomAdapter Constructor *****************/
    public NavHistoryAdapter(Activity a, ArrayList<NavigationHistory> d) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        mHighlightedPositions = new boolean[d.size()];

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
    public NavigationHistory getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /** THIS SOLVED SCROLLING ISSUE WITH FALSE VALUES **/
    @Override
    public int getItemViewType(int position) {
        return this.getItem(position).getFriendProfileImageString().equalsIgnoreCase("") ? ITEM_VIEW_TYPE_FRIEND : ITEM_VIEW_TYPE_MINE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView nameTextView;
        public TextView descTextView;
        public TextView optionTextView;
        public RoundedImageView profileImageView;
        public ImageView iconImageView;
        public LinearLayout optionLayout;
        public LinearLayout deleteLayout;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****************** Inflate list_row.xml for each row ******************/
            vi = inflater.inflate(R.layout.list_row_nav_history, null);


            /****************** View Holder Object to contain list_row.xml file elements *******************/
            holder = new ViewHolder();
            holder.nameTextView = (TextView) vi.findViewById(R.id.name_text_view);
            holder.descTextView = (TextView) vi.findViewById(R.id.desc_text_view);
            holder.optionTextView = (TextView) vi.findViewById(R.id.option_text_view);
            holder.iconImageView = (ImageView) vi.findViewById(R.id.icon_image_view);
            holder.profileImageView = (RoundedImageView) vi.findViewById(R.id.profile_image_view);
            holder.optionLayout = (LinearLayout) vi.findViewById(R.id.option_button_layout);
            holder.deleteLayout = (LinearLayout) vi.findViewById(R.id.del_button_layout);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        }
        else{
            holder=(ViewHolder)vi.getTag();
        }

        if(data.size()<=0){
            holder.nameTextView.setText("Please Add Locations.");
        }
        else{
            /****************** Get Each Model Object From Array List *******************/
            tempValues = null;
            tempValues = (NavigationHistory) data.get(position);

            /****************** Set Model Values in Holder Elements *****************/
            holder.nameTextView.setText(tempValues.getFriendName());
            String descString = "";
            if(tempValues.getAction().equalsIgnoreCase("object")){
                descString = "I requested location - ";
            }
            else{
                descString = "I sent location - ";
            }

            descString = descString+getDate(Long.parseLong(tempValues.getNavStartTime()));

            holder.descTextView.setText(descString);
//            holder.descriptionTextView.setText(tempValues.getDescription());
//            holder.iconImageView.setBackground(activity.getResources().getDrawable(tempValues.getIconImage()));
            holder.profileImageView.setTag(position);
            holder.optionLayout.setTag(position);
            holder.deleteLayout.setTag(position);


            /***************** Set Item Click Listner for LayoutInflator for each row ******************/
            vi.setOnClickListener(new OnItemClickListener(position));

            if(mHighlightedPositions[position]) {
                holder.optionLayout.setVisibility(View.VISIBLE);
                holder.deleteLayout.setVisibility(View.VISIBLE);
            }else {
                holder.optionLayout.setVisibility(View.GONE);
                holder.deleteLayout.setVisibility(View.GONE);
            }

//            Constants.setImageViewFromString(tempValues.getFriendProfileImageString(), holder.profileImageView);
            Constants.setImageViewFromString(tempValues.getFriendProfileImageString().equalsIgnoreCase("") ? "" : tempValues.getFriendProfileImageString(), holder.profileImageView);
        }



        return vi;
    }


    private String getDate(long time) {

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MMM - HH:mm");
        java.util.Date currenTimeZone=new java.util.Date((long)time);
        return sdf.format(currenTimeZone);


//        Calendar cal = Calendar.getInstance();
//        TimeZone tz = cal.getTimeZone();//get your local time zone.
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
//        sdf.setTimeZone(tz);//set time zone.
//        String localTime = sdf.format(new Date(time) * 1000);
//        Date date = new Date();
//        String dateString = "";
//        try {
//            dateString = sdf.parse(localTime);//get local date
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return localTime;
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
            LinearLayout deleteButton = (LinearLayout)arg0.findViewById(R.id.del_button_layout);

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
                        deleteButton.setVisibility(View.GONE);
                        mHighlightedPositions[i] = false;
                    }else {
                        optionButton.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.VISIBLE);
                        optionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainPrivateActivity act = (MainPrivateActivity) activity;
                                NavigationHistory loca = data.get(mPosition);
                                act.onLoadNavigationHistoryDetail(loca);


                            }
                        });
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainPrivateActivity act = (MainPrivateActivity)activity;
                                NavigationHistory loca = data.get(mPosition);
                                act.onDeleteNavigationHistory(loca);

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
