package com.stanstudios.oneoff;

import android.app.ActionBar;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ScrollView;

public class MainActivity extends Activity implements View.OnClickListener {
    DevicePolicyManager devicePolicyManager;
    ComponentName dmsDeviceAdmin;
    SharedPreferences appPreferences;
    private WebView webView;
    private ScrollView scrollView;
    boolean isAppInstalled = false;
    boolean isAccept = false;
    static final String TAG = "OneOffReceiver";
    static final int ACTIVATION_REQUEST = 47; // identifies our request id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            actionBar = getActionBar();
        }
        if (actionBar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                actionBar.hide();
            }
        }
        appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isAccept = appPreferences.getBoolean("isAccept", false);
        Log.w("onCreate", "onCreate");
        findViewById(R.id.btnAccept).setOnClickListener(this);
        findViewById(R.id.btnNo).setOnClickListener(this);
        if(isAccept) init();
        else loadPolicy();
    }

    private void loadPolicy() {
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://indico.vn:8101/privacy_policy.html");
    }

    private void init() {

        isAppInstalled = appPreferences.getBoolean("isAppInstalled", false);
        if (isAppInstalled == false) {//If not install then create shortcut
            Intent shortcutIntent = new Intent(getApplicationContext(),
                    MainActivity.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Off");
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(
                            getApplicationContext(), R.mipmap.ic_launcher));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(intent);
            /**
             * Make preference true
             */
            SharedPreferences.Editor editor = appPreferences.edit();
            editor.putBoolean("isAppInstalled", true);
            editor.commit();

        }
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        dmsDeviceAdmin = new ComponentName(this, OneOffReceiver.class);
        Intent intent = new Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                dmsDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Your boss told you to do this");
        startActivityForResult(intent, ACTIVATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVATION_REQUEST:
                if (resultCode != Activity.RESULT_OK) {
                    Intent intent = new Intent(
                            DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                            dmsDeviceAdmin);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            "Your boss told you to do this");
                    startActivityForResult(intent, ACTIVATION_REQUEST);
                } else {
                    devicePolicyManager.lockNow();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        Log.w("onPause", "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.w("onResume", "onResume");
        super.onResume();
    }

    @Override
    protected void onRestart() {
        Log.w("onRestart", "onRestart");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.w("onStart", "onStart");
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.w("onDestroy", "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.w("onStop", "onStop");
        finish();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAccept:
                SharedPreferences.Editor editor = appPreferences.edit();
                editor.putBoolean("isAccept", true);
                editor.commit();
                init();
                break;
            case R.id.btnNo:
                finish();
                break;
            default:
                break;
        }
    }
}
