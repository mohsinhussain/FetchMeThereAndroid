package com.webmedia7.mohsinhussain.fetchmethere.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.webmedia7.mohsinhussain.fetchmethere.Activities.ChooseUserActivity;
import com.webmedia7.mohsinhussain.fetchmethere.Model.UserOptionModel;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.util.ArrayList;

/**
 * Created by mohsinhussain on 3/22/15.
 */
public class UserOptionAdapter extends BaseAdapter implements View.OnClickListener {

    /************** Declare used variables ******************/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    UserOptionModel tempValues = null;
    int i = 0;


    /*************  CustomAdapter Constructor *****************/
    public UserOptionAdapter(Activity a, ArrayList d,Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }



    @Override
    public int getCount() {
        if(data.size()<=0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView titleTextView;
        public TextView descriptionTextView;
        public ImageView iconImageView;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****************** Inflate list_row.xml for each row ******************/
            vi = inflater.inflate(R.layout.list_row_user_option, null);


            /****************** View Holder Object to contain list_row.xml file elements *******************/
            holder = new ViewHolder();
            holder.titleTextView = (TextView) vi.findViewById(R.id.title_text_view);
            holder.descriptionTextView = (TextView) vi.findViewById(R.id.description_text_view);
            holder.iconImageView = (ImageView) vi.findViewById(R.id.icon_image_view);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else{
            holder=(ViewHolder)vi.getTag();
        }

        if(data.size()<=0){
            holder.titleTextView.setText("No Data");
        }
        else{
            /****************** Get Each Model Object From Array List *******************/
            tempValues = null;
            tempValues = (UserOptionModel) data.get(position);

            /****************** Set Model Values in Holder Elements *****************/
            holder.titleTextView.setText(tempValues.getTitle());
            holder.descriptionTextView.setText(tempValues.getDescription());
            holder.iconImageView.setBackground(activity.getResources().getDrawable(tempValues.getIconImage()));

            /***************** Set Item Click Listner for LayoutInflator for each row ******************/
            vi.setOnClickListener(new OnItemClickListener(position));
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


            ChooseUserActivity act = (ChooseUserActivity)activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            act.onItemClick(mPosition);
        }
    }
}
