package com.hither.lianxiren;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Administrator on 2016/9/7.
 */
public class MyApplication extends Application {
    public static RequestQueue requestQueue;
    public static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        requestQueue = Volley.newRequestQueue(applicationContext);
    }
}
