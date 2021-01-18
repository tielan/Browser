package com.chinacreator.browser.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chinacreator.browser.MainActivity;

public class AutoStartBroadcastReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Intent intent2 = new Intent(context, MainActivity.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
    }
}
