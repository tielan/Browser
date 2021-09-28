package com.chinacreator.browser.utils;

import android.content.Context;

import java.util.Map;

public class AppUtils {
    public static  Map<String,String> getAppInfo(Context context){
        return  WalleChannelReader.getChannelInfoMap(context);
    }
}
