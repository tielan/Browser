package com.chinacreator.browser;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.chinacreator.browser.crash.CrashHandler;
import com.chinacreator.browser.utils.FileUtils;
import com.tencent.bugly.Bugly;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.yanzhenjie.andserver.util.IOUtils;

import java.io.File;
import java.util.HashMap;

public class BrowserApplication extends Application {


    private static BrowserApplication mInstance;

    private File mRootDir;

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
