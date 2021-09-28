package com.chinacreator.browser;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.chinacreator.browser.utils.FileUtils;
import com.tencent.bugly.Bugly;
import com.yanzhenjie.andserver.util.IOUtils;

import java.io.File;

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
        mRootDir = new File(mRootDir, "browser");
        IOUtils.createFolder(mRootDir);
    }
}
