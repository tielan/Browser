package com.chinacreator.browser.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigUtils {
    private Context mContext;
    private static ConfigUtils instance = new ConfigUtils();

    public static ConfigUtils getInstance() {
        return instance;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public void set(String key, String value) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String get(String key) {
        SharedPreferences pre = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getString(key, null);
    }
}
