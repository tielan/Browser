package com.chinacreator.browser;

import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.chinacreator.browser.detector.MultiTouchDetector;
import com.chinacreator.browser.detector.MultiTouchListener;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private AlertDialog mDialog;
    private MultiTouchDetector multiTouchDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//这行代码一定要在setContentView之前，不然会闪退
        setContentView(R.layout.activity_main);
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
    }

    private View.OnTouchListener listener = (v, event) -> {
        multiTouchDetector.onTouchEvent(event);
        return false;
    };


    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        loadUrl();
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
        inputServer.setText(getUrl());
        inputServer.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inputServer)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialog = null;
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mDialog = null;
                String text = inputServer.getText().toString();
                setUrl(text);
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDialog = null;
            }
        });
        mDialog = builder.show();
    }

    private void loadUrl() {
        if (!TextUtils.isEmpty(getUrl())) {
            webView.loadUrl(getUrl());
        }
    }

    private void setUrl(String url) {
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("url", url);
        editor.apply();
        loadUrl();
    }

    private String getUrl() {
        SharedPreferences pre = getSharedPreferences("data", MODE_PRIVATE);
        return pre.getString("url", null);
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
}
