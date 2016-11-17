package me.anikraj.campussecrets;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by anikr on 8/25/2016.
 */
public class MyApplication extends Application {

    public MyApplication() {
        // MyApplication method fires only once per application start.
        // getApplicationContext returns null here
    }

    @Override
    public void onCreate() {


        // this method fires once as well as constructor
        // but also application has context here
        if (!FirebaseApp.getApps(this).isEmpty())
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        super.onCreate();
    }
}