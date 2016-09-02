package com.idt.test.app.function;

import android.content.Context;
import android.content.SharedPreferences;

import com.idt.test.app.R;

/**
 * Created by Omid Heshmatinia on 9/2/2016.
 */
public class MyPreference {

    public static MyPreference mInstance;
    public SharedPreferences sf;
    final static String APP_NAME="IDT";
    public Context mContext;

    /**
     * Should be called in Application class to prevent memory Leak
     * @param ct
     * @return
     */
    public static MyPreference getInstance(Context ct){
        if(mInstance==null){
            mInstance=new MyPreference();
            mInstance.mContext=ct;
            mInstance.sf=ct.getSharedPreferences(APP_NAME,0);
        }
        return mInstance;
    }

    /**
     * Get Last downloaded url
     * @return String url of last file
     */
    public String getLastUrl(){
        return mInstance.sf.getString(mInstance.mContext.getString(R.string.pref_url_key),"");
    }

    /**
     * Set last downloaded url
     * @param txt
     */
    public void setLastUrl(String txt){
        mInstance.sf.edit().putString(mInstance.mContext.getString(R.string.pref_url_key),txt).commit();
    }

    /**
     * Indicate whether its first visit or not
     * @return
     */
    public Boolean isFirstVisit(){
        return mInstance.sf.getBoolean(mInstance.mContext.getString(R.string.pref_first_visit_key),true);
    }


    /**
     * Change first visit status
     * @return
     */
    public void setFirstVisit(Boolean type){
        mInstance.sf.edit().putBoolean(mInstance.mContext.getString(R.string.pref_first_visit_key),type).commit();
    }
}
