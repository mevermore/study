package com.smasher.media;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.smasher.zxing.activity.ZXingLibrary;

/**
 * Created on 2019/6/6.
 *
 * @author moyu
 */
public class SmasherApplication extends MultiDexApplication {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);
    }


}
