package com.chinacreator.browser;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.chinacreator.browser.detector.MultiTouchDetector;
import com.chinacreator.browser.detector.MultiTouchListener;
import com.chinacreator.browser.utils.ConfigUtils;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private AlertDialog mDialog;
    private MultiTouchDetector multiTouchDetector;
    private int screenOrientation = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//这行代码一定要在setContentView之前，不然会闪退
        setContentView(R.layout.activity_main);
        ConfigUtils.getInstance().init(this);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginsEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                new Thread(() -> webView.loadUrl(url));
                return false;
            }
        });
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                //view.loadUrl("file:///android_assets/error_handle.html");
//            }
//        });
        webView.setOnTouchListener(listener);
        multiTouchDetector = new MultiTouchDetector(new MultiTouchListener() {
            @Override
            public void onTapUp(int numFingers) {

            }

            @Override
            public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, int numFingers) {
                if (Math.abs(distanceX) > 20 && numFingers == 2) {
                    showDialog();
                }
            }
        });
        showDialog();
    }

    private View.OnTouchListener listener = (v, event) -> {
        multiTouchDetector.onTouchEvent(event);
        return false;
    };


    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        loadConfig();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    private void showDialog() {
        if (mDialog != null) return;
        final EditText inputServer = new EditText(this);
        inputServer.setHint("http://172.16.17.1:8080");
        inputServer.setText(ConfigUtils.getInstance().get("url"));
        inputServer.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int o = 0;
        if (!TextUtils.isEmpty(ConfigUtils.getInstance().get("screenOrientation"))) {
             o = Integer.parseInt(ConfigUtils.getInstance().get("screenOrientation"));
        }
        builder.setSingleChoiceItems(new String[]{"默认方向","竖屏","横屏"}, o, (dialog, which) -> {
            screenOrientation = which;
        });
        builder.setView(inputServer)
                .setNegativeButton("取消", (dialog, which) -> {
                    screenOrientation = -1;
                    mDialog = null;
                    dialog.dismiss();
                });
        builder.setPositiveButton("确定", (dialog, which) -> {
            mDialog = null;
            String text = inputServer.getText().toString();
            ConfigUtils.getInstance().set("url", text);
            if(screenOrientation > -1){
                ConfigUtils.getInstance().set("screenOrientation", screenOrientation + "");
            }
            screenOrientation = -1;
            loadConfig();
        });
        builder.setOnDismissListener(dialog -> mDialog = null);
        mDialog = builder.show();
    }

    private void loadConfig() {
        if (!TextUtils.isEmpty(ConfigUtils.getInstance().get("url"))) {
            webView.loadUrl(ConfigUtils.getInstance().get("url"));
        }
        if (!TextUtils.isEmpty(ConfigUtils.getInstance().get("screenOrientation"))) {
            int o = Integer.parseInt(ConfigUtils.getInstance().get("screenOrientation"));
            if(o == 0){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }else  if(o == 1){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }else  if(o == 2){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {  //表示按返回键
            webView.goBack();   //后退
        } else {
            super.onBackPressed();
        }
        return;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void showMessage(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
