package me.anikraj.campussecrets.views;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import me.anikraj.campussecrets.CustomViewPager;
import me.anikraj.campussecrets.R;

public class MainActivity extends FragmentActivity {
    private static final int RC_SIGN_IN = 2772 ;
    private static final int NUM_PAGES = 3;
    private CustomViewPager mPager;
    private PagerAdapter mPagerAdapter;
    FloatingActionButton fab;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    Toolbar toolbar;
    Editor editor=new Editor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  toolbar  = (Toolbar) findViewById(R.id.toolbar);
     //   toolbar.setTitle("Campus Secrets");
     //   FirebaseCrash.report(new Exception("My first Android non-fatal error"));



        fab = (FloatingActionButton)findViewById(R.id.fab);


        checkauth();
        playintroifneeded();
        // login();

        pager_init();
       // bottombar_init(savedInstanceState);
        if(getIntent().getStringExtra("tag")!=null) {
            if (getIntent().getStringExtra("tag").compareTo("camera") == 0) {
                mPager.setCurrentItem(1, false);
            }
        }
    }



    private void checkauth(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))

                            .build(),

                    RC_SIGN_IN);
        }
        else {
            Log.d("auth","yes");
        }
    }

    private void playintroifneeded(){
        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {
                    //if(true){
                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, Splash.class);
                    startActivity(i);


                    finish();
                }
            }
        });

        // Start the thread
        t.start();

    }
    private void bottombar_init(Bundle savedInstanceState){
//        mBottomBar = BottomBar.attach(this, savedInstanceState);mBottomBar.noTopOffset();
//
//        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
//            @Override
//            public void onMenuTabSelected(@IdRes int menuItemId) {
//                if (menuItemId == R.id.bottomBarHome) {
//                    // The user selected item number one.
//                    mPager.setCurrentItem(0, false);
//                }
//                else if (menuItemId == R.id.bottomBarStrends) {
//                    // The user selected item number one.
//                    mPager.setCurrentItem(1, false);
//                }
//                else  if (menuItemId == R.id.bottomBarEvents) {
//                    // The user selected item number one.
//                    mPager.setCurrentItem(2, false);
//                }
//                else  if (menuItemId == R.id.bottomBarSettings) {
//                    // The user selected item number one.
//                    mPager.setCurrentItem(3, false);
//                }
//
//            }
//
//            @Override
//            public void onMenuTabReSelected(@IdRes int menuItemId) {
//                if (menuItemId == R.id.bottomBarHome) {
//                    // The user reselected item number one, scroll your content to top.
//                }
//            }
//        });
//
//        // Setting colors for different tabs when there's more than three of them.
//        // You can set colors for tabs in three different ways as shown below.
//        mBottomBar.mapColorForTab(0,  ContextCompat.getColor(this, R.color.colorPrimaryDark));
//        mBottomBar.mapColorForTab(1,  ContextCompat.getColor(this, R.color.colorPrimary));
//        mBottomBar.mapColorForTab(2,  ContextCompat.getColor(this, R.color.colorPrimaryDark));
//        mBottomBar.mapColorForTab(3,  ContextCompat.getColor(this, R.color.colorPrimary));
//        //mBottomBar.mapColorForTab(4, "#FF9800");
//        int paddingPixel = 8;
//        float density = this.getResources().getDisplayMetrics().density;
//        int paddingDp = (int)(paddingPixel * density);
//        //mBottomBar.setPadding(0,0,0,paddingDp);
    }


    private void pager_init(){
        mPager = (CustomViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
       // mPager.setPagingEnabled(false);
        mPager.setCurrentItem(0, false);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==1) showfab();
                else hidefab();

                if(position==0||position==2)hidekeybaord();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void hidefab(){
        if(fab!=null){
            fab.hide();
        }
    }
    private void showfab(){
        if(fab!=null){
            fab.show();
        }
    }
    private void hidekeybaord(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position==0){
              //  hidefab();
                return new ViewNotes();
            }
            else if (position==1){
               // showfab();
                return editor;
            }
            else if (position==2){
            //    hidefab();
                return new Home();
            }

            else return new Home();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }


    }





    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // user is signed in!
                //startActivity(new Intent(this, WelcomeBackActivity.class));
               // finish();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Name, email address, and profile photo Url
                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    Uri photoUrl = user.getPhotoUrl();
            }
            }
                else {
                // user is not signed in. Maybe just wait for the user to press
                // "sign in" again, or show a message
            }
        }
    }



    public void setActionBarTitle(String title){
        toolbar.setTitle(title);
    }

    public void logout(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(MainActivity.this, Splash.class));
                        finish();
                    }
                });
    }

}
