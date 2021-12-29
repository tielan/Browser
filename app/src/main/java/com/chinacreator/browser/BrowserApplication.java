package com.chinacreator.browser;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chinacreator.browser.crash.CrashHandler;
import com.chinacreator.browser.utils.FileUtils;
import com.tencent.bugly.Bugly;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.yanzhenjie.andserver.util.IOUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BrowserApplication extends Application {


    private static BrowserApplication mInstance;

    private File mRootDir;
    private Map<String, String> devicesInfo = new HashMap<String, String>();

    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "c3e18da1cf", false);
        if (mInstance == null) {
            mInstance = this;
            initRootPath(this);
        }
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        collectDeviceInfo(this);
    }

    public Map<String, String> getDevicesInfo() {
        return devicesInfo;
    }

    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                devicesInfo.put("versionName", versionName);
                devicesInfo.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                devicesInfo.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
            }
        }
    }

    @NonNull
    public static BrowserApplication getInstance() {
        return mInstance;
    }

    @NonNull
    public File getRootDir() {
        return mRootDir;
    }

    private void initRootPath(Context context) {
        if (mRootDir != null) {
            return;
        }
        if (FileUtils.storageAvailable()) {
            mRootDir = Environment.getExternalStorageDirectory();
        } else {
            mRootDir = context.getFilesDir();
        }
        mRootDir = new File(mRootDir, "html");
        IOUtils.createFolder(mRootDir);
    }
}
