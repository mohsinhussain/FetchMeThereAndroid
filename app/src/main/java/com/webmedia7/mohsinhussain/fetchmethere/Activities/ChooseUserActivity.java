package com.webmedia7.mohsinhussain.fetchmethere.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.webmedia7.mohsinhussain.fetchmethere.Adapters.UserOptionAdapter;
import com.webmedia7.mohsinhussain.fetchmethere.Classes.Constants;
import com.webmedia7.mohsinhussain.fetchmethere.FetchMeThere;
import com.webmedia7.mohsinhussain.fetchmethere.Model.UserOptionModel;
import com.webmedia7.mohsinhussain.fetchmethere.R;

import java.util.ArrayList;


public class ChooseUserActivity extends Activity {

    ListView list;
    TextView titleTextView;
    UserOptionAdapter mAdapter;
    public ChooseUserActivity customListView = null;
    public ArrayList<UserOptionModel> userOptionModelArrayList = new ArrayList<UserOptionModel>();
    boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        customListView = this;


        Intent loginActivityIntent = getIntent();
        isLogin = loginActivityIntent.getExtras().getBoolean("isLogin");

        titleTextView = (TextView) findViewById(R.id.header_text_view);

        if(isLogin){
            titleTextView.setText("I WANT TO LOGIN AS:");
        }
        else{
            titleTextView.setText("I WANT TO REGISTER AS:");
        }

        /********* Take some data in Array List ************/
        setListData();

        Resources res = getResources();
        list = (ListView) findViewById(R.id.user_option_list_view); //List defined in XML

        /************* Create Custom Adapter ***************/
        mAdapter = new UserOptionAdapter(customListView, userOptionModelArrayList, res);
        list.setAdapter(mAdapter);
    }


    /****** Function to set data in ArrayList *************/
    public void setListData()
    {
        userOptionModelArrayList.add(new UserOptionModel("PIVATE USER", "This is the description for private user login details.", R.drawable.private_user_icon));
//        userOptionModelArrayList.add(new UserOptionModel("BUSINESS USER", "This is the description for private user login details.", R.drawable.business_user_icon));
//        userOptionModelArrayList.add(new UserOptionModel("AGENT", "This is the description for private user login details.", R.drawable.agent_user_icon));

//        for (int i = 0; i < 11; i++) {
//
//            final UserOptionModel userOption = new UserOptionModel();
//
//            /******* Firstly take data in model object ******/
//            userOption.setTitle("Company "+i);
//            userOption.setImage("image"+i);
//            userOption.setUrl("http:\\www."+i+".com");
//
//            /******** Take Model Object in ArrayList **********/
//            CustomListViewValuesArr.add( sched );
//        }

    }

    /*****************  This function used by adapter ****************/
    public void onItemClick(int mPosition)
    {
        UserOptionModel tempValues = ( UserOptionModel ) userOptionModelArrayList.get(mPosition);

        if (!isLogin){
            Intent registerIntent = new Intent(ChooseUserActivity.this, RegisterActivity.class);
            String userType = "p";
            if(mPosition==0){
                userType = "p";
            }
            else if(mPosition==1){
                userType = "b";
            }
            else if(mPosition==2){
                userType = "a";
            }
            registerIntent.putExtra(Constants.CURRENT_USER_TYPE_STRING_INTENT,userType);
            startActivity(registerIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Tracker t = FetchMeThere.getInstance().tracker;
        t.setScreenName("Choose User Type");
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
