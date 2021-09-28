package com.chinacreator.browser.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.chinacreator.browser.event.MessageEvent;

import org.w3c.dom.Text;

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

    public MessageEvent getConfig() {
        MessageEvent event = new MessageEvent();
        event.setUrl(ConfigUtils.getInstance().get(MessageEvent.C_url));
        event.setOrientation(ConfigUtils.getInstance().get(MessageEvent.C_screenOrientation));
        event.setShowBack(ConfigUtils.getInstance().get(MessageEvent.C_showBack));
        return event;
    }

    public void saveConfig(MessageEvent event) {
        if(event != null){
            if (!TextUtils.isEmpty(event.getUrl())) {
                set(MessageEvent.C_url, event.getUrl());
            }
            if (!TextUtils.isEmpty(event.getOrientation())) {
                set(MessageEvent.C_screenOrientation, event.getOrientation());
            }
            if (!TextUtils.isEmpty(event.getShowBack())) {
                set(MessageEvent.C_showBack, event.getShowBack());
            }
        }

    }
}
