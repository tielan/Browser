package com.chinacreator.browser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chinacreator.browser.event.ExitAppEvent;
import com.chinacreator.browser.event.MessageEvent;
import com.chinacreator.browser.utils.ConfigUtils;
import com.chinacreator.browser.utils.NetUtils;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private static final long COLLAPSE_SB_PERIOD = 100;
    private RelativeLayout backLy;
    private View backView;
    private View homeView;
    private Server mServer;

    private View infoView;
    private TextView infoTV;
    private AlertDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//这行代码一定要在setContentView之前，不然会闪退
        setContentView(R.layout.activity_main);
        ConfigUtils.getInstance().init(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        webView = findViewById(R.id.webview);
        backLy = findViewById(R.id.backly);
        backView = findViewById(R.id.back);
        homeView = findViewById(R.id.home);
        infoView = findViewById(R.id.info);
        infoTV = findViewById(R.id.infoTv);


        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
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
        backView.setOnClickListener(onClickListener);
        homeView.setOnClickListener(onClickListener);
        webView.addJavascriptInterface(this, "android");
        onHideOption();
        startServer();
        EventBus.getDefault().register(this);
        loadConfig();
    }

    private void startServer() {
        mServer = AndServer.webServer(this)
                .port(8080)
                .timeout(10, TimeUnit.SECONDS)
                .build();
        mServer.startup();
        String infoTv = "打开电脑浏览器访问：http://" + NetUtils.getLocalIPAddress().getHostAddress() + ":8080";
        infoTV.setText(infoTv);
        infoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
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
        MessageEvent event = new MessageEvent();
        builder.setSingleChoiceItems(new String[]{"默认方向","竖屏","横屏"}, o, (dialog, which) -> {
            event.setOrientation(which+"");
        });
        builder.setView(inputServer)
                .setNegativeButton("取消", (dialog, which) -> {
                    mDialog = null;
                    dialog.dismiss();
                });
        builder.setPositiveButton("确定", (dialog, which) -> {
            mDialog = null;
            String text = inputServer.getText().toString();
            event.setUrl(text);
            ConfigUtils.getInstance().saveConfig(event);
            loadConfig();
        });
        mDialog = builder.show();
    }


    /**
     * 接收到消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event != null) {
            ConfigUtils.getInstance().saveConfig(event);
            loadConfig();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ExitAppEvent event) {
        if (event.isExitApp()) {
            exitSys();
        } else if (event.isRestartApp()) {
            restartApp();
        }

    }

    @JavascriptInterface
    public void hiddenNav() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                backLy.setVisibility(View.GONE);
            }
        });
    }

    @JavascriptInterface
    public void showNav() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                backLy.setVisibility(View.VISIBLE);
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.home) {
                loadUrl();
            } else if (v.getId() == R.id.back) {
                webView.goBack();
            }
        }
    };
    private void loadUrl(){
        MessageEvent event = ConfigUtils.getInstance().getConfig();
        if (event != null && !TextUtils.isEmpty(event.getUrl())) {
            if (event.getUrl().startsWith("http")) {
                webView.loadUrl(event.getUrl());
            } else {
                webView.loadUrl("file:" + event.getUrl());
            }
            infoView.setVisibility(View.GONE);
        }
    }

    private void loadConfig() {
        MessageEvent event = ConfigUtils.getInstance().getConfig();
        loadUrl();
        if (!TextUtils.isEmpty(event.getOrientation())) {
            int o = Integer.parseInt(event.getOrientation());
            if (o == 0) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            } else if (o == 1) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (o == 2) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
        if (!TextUtils.isEmpty(event.getShowBack())) {
            if ("GONE".equals(event.getShowBack())) {
                backLy.setVisibility(View.GONE);
            } else if ("VISIBLE".equals(event.getShowBack())) {
                backLy.setVisibility(View.VISIBLE);
            }
        }
    }

    private void exitSys() {
        finish();
        System.exit(0);
    }

    private void restartApp() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {  //表示按返回键
            webView.goBack();   //后退
        } else {
           // super.onBackPressed();
        }
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServer.shutdown();
        if (webView != null) {
            QbSdk.clearAllWebViewCache(this,true);
        }
        EventBus.getDefault().unregister(this);
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


    private Handler hideHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1000) {
                collapse(MainActivity.this, true);
            }
        }
    };

    private void onHideOption() {
        getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | 2 | 4 | 2048);
        this.hideHandler.sendEmptyMessageDelayed(1000, COLLAPSE_SB_PERIOD);
    }

    public static void collapse(Activity activity, boolean isShow) {
        Window window = activity.getWindow();
        if (isShow) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            window.setAttributes(attributes);
            window.addFlags(512);
            window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | 2 | 4 | 2048);
            attributes.flags |= 1024;
            return;
        }
        WindowManager.LayoutParams attributes2 = window.getAttributes();
        attributes2.flags &= 1024;
        window.setAttributes(attributes2);
        window.clearFlags(512);
    }
}
