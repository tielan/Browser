package com.chinacreator.browser;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.chinacreator.browser.utils.ShakeUtils;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private ShakeUtils mShakeUtils;
    private AlertDialog mDialog;
    private final int DOUBLE_TAP_TIMEOUT = 200;
    private MotionEvent mCurrentDownEvent;
    private MotionEvent mPreviousUpEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//这行代码一定要在setContentView之前，不然会闪退
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        webView = findViewById(R.id.webview);
        mShakeUtils = new ShakeUtils(this);
        mShakeUtils.setOnShakeListener(new ShakeUtils.OnShakeListener() {
            @Override
            public void onShake() {
                // showDialog();
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadurlLocalMethod(view, url);
                return false;
            }
        });
        webView.setOnTouchListener(listener);

    }

    private View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mPreviousUpEvent != null
                        && mCurrentDownEvent != null
                        && isConsideredDoubleTap(mCurrentDownEvent,
                        mPreviousUpEvent, event)) {
                    showDialog();
                }
                mCurrentDownEvent = MotionEvent.obtain(event);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mPreviousUpEvent = MotionEvent.obtain(event);
            }
            return false;
        }
    };

    private boolean isConsideredDoubleTap(MotionEvent firstDown,
                                          MotionEvent firstUp, MotionEvent secondDown) {
        if (secondDown.getEventTime() - firstUp.getEventTime() > DOUBLE_TAP_TIMEOUT) {
            return false;
        }
        int deltaX = (int) firstUp.getX() - (int) secondDown.getX();
        int deltaY = (int) firstUp.getY() - (int) secondDown.getY();
        return deltaX * deltaX + deltaY * deltaY < 10000;
    }


    public void loadurlLocalMethod(final WebView webView, final String url) {
        new Thread(() -> webView.loadUrl(url));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUrl();
        mShakeUtils.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShakeUtils.onPause();
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
