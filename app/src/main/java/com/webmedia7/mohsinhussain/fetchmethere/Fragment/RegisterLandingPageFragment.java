package com.webmedia7.mohsinhussain.fetchmethere.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.webmedia7.mohsinhussain.fetchmethere.Activities.RegisterActivity;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.R;

/**
 * Created by mohsinhussain on 4/21/15.
 */
public class RegisterLandingPageFragment extends Fragment {

    private static final String ARG_PARAM1 = "position";
    private int mPositon = 0;

    // TODO: Rename and change types and number of parameters
    public static RegisterLandingPageFragment newInstance(int position) {
        RegisterLandingPageFragment fragment = new RegisterLandingPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    public RegisterLandingPageFragment(){

    }


    private RelativeLayout mainLayout;
    private TextView descriptionTextView;
    private TextView headerTextView;
    private ImageView firstPagingImage;
    private ImageView secondPagingImage;
    private ImageView thirdPagingImage;
    private Button registerButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_registration_landing_page, container, false);

        mainLayout = (RelativeLayout) rootView.findViewById(R.id.main_relative_layout);
        headerTextView = (TextView) rootView.findViewById(R.id.head_text_view);
        descriptionTextView = (TextView) rootView.findViewById(R.id.desc_text_view);
        firstPagingImage = (ImageView) rootView.findViewById(R.id.first_paging_image_view);
        secondPagingImage = (ImageView) rootView.findViewById(R.id.seconf_paging_image_view);
        thirdPagingImage = (ImageView) rootView.findViewById(R.id.third_paging_image_view);
        registerButton = (Button) rootView.findViewById(R.id.register_button);



        if (getArguments() != null) {
            mPositon = getArguments().getInt(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if(mPositon==0){
            mainLayout.setBackground(getResources().getDrawable(R.drawable.register_bg_first));
            descriptionTextView.setText(getResources().getString(R.string.reg_first_description));
            headerTextView.setText(getResources().getString(R.string.reg_first_header));
            firstPagingImage.setBackground(getResources().getDrawable(R.drawable.paging_image_selected));
            secondPagingImage.setBackground(getResources().getDrawable(R.drawable.paging_image_normal));
            thirdPagingImage.setBackground(getResources().getDrawable(R.drawable.paging_image_normal));
        }
        else if(mPositon==1){
            mainLayout.setBackground(getResources().getDrawable(R.drawable.register_bg_second));
            descriptionTextView.setText(getResources().getString(R.string.reg_sec_description));
            headerTextView.setText(getResources().getString(R.string.reg_sec_header));
            firstPagingImage.setBackground(getResources().getDrawable(R.drawable.paging_image_normal));
            secondPagingImage.setBackground(getResources().getDrawable(R.drawable.paging_image_selected));
            thirdPagingImage.setBackground(getResources().getDrawable(R.drawable.paging_image_normal));
        }
        else if(mPositon==2) {
            mainLayout.setBackground(getResources().getDrawable(R.drawable.registeration_bg_third));
            descriptionTextView.setText(getResources().getString(R.string.reg_third_description));
            headerTextView.setText(getResources().getString(R.string.reg_third_header));
            firstPagingImage.setBackground(getResources().getDrawable(R.drawable.paging_image_normal));
            secondPagingImage.setBackground(getResources().getDrawable(R.drawable.paging_image_normal));
            thirdPagingImage.setBackground(getResources().getDrawable(R.drawable.paging_image_selected));

        }
        else{
            mainLayout.setBackground(getResources().getDrawable(R.drawable.register_bg_first));
            descriptionTextView.setText(getResources().getString(R.string.reg_first_description));
            headerTextView.setText(getResources().getString(R.string.reg_first_header));
            firstPagingImage.setBackground(getResources().getDrawable(R.drawable.paging_image_selected));
            secondPagingImage.setBackground(getResources().getDrawable(R.drawable.paging_image_normal));
            thirdPagingImage.setBackground(getResources().getDrawable(R.drawable.paging_image_normal));
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
                String userType = "p";
//                if(mPosition==0){
//                    userType = "p";
//                }
//                else if(mPosition==1){
//                    userType = "b";
//                }
//                else if(mPosition==2){
//                    userType = "a";
//                }
                registerIntent.putExtra(Constants.CURRENT_USER_TYPE_STRING_INTENT,userType);
                startActivity(registerIntent);
//                Log.v("ada", "goto next page");
//                Intent chooseUserActivtyIntent = new Intent(getActivity(), ChooseUserActivity.class);
//                chooseUserActivtyIntent.putExtra("isLogin", false);
//                startActivity(chooseUserActivtyIntent);
            }
        });

        return rootView;
    }
}
