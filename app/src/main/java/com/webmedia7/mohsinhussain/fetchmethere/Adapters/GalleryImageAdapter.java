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

import com.webmedia7.mohsinhussain.fetchmethere.Fragment.AddEditLocationFragment;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.util.ArrayList;

/**
 * Created by mohsinhussain on 3/22/15.
 */
public class GalleryImageAdapter extends BaseAdapter {

    /************** Declare used variables ******************/
    private Activity activity;
    private ArrayList<String> data;
    private static LayoutInflater inflater = null;
    String tempValues = null;
    int i = 0;
    String action = "";
    AddEditLocationFragment parentFragment;


    /*************  CustomAdapter Constructor *****************/
    public GalleryImageAdapter(Activity a, ArrayList<String> d, String action, AddEditLocationFragment parentFragment) {

        /********** Take passed values **********/
        activity = a;
        if(d!=null){
            data=d;
        }
        else{
            data = new ArrayList<String>();
        }
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
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
        public ImageView imageView;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****************** Inflate list_row.xml for each row ******************/
            vi = inflater.inflate(R.layout.gallery_item_image, null);


            /****************** View Holder Object to contain list_row.xml file elements *******************/
            holder = new ViewHolder();
            holder.imageView = (ImageView) vi.findViewById(R.id.image_view);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        }
        else{
            holder=(ViewHolder)vi.getTag();
        }

//        if(position==data.size()){
//            holder.imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.camera_bg));
//        }
//        else{
            /****************** Get Each Model Object From Array List *******************/
            tempValues = null;
            tempValues = (String) data.get(position);

            /****************** Set Model Values in Holder Elements *****************/
            if(tempValues!=null){
                byte[] byteArray = Base64.decode(tempValues, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                holder.imageView.setImageBitmap(bitmap);
            }

//        }



        return vi;
    }


}
