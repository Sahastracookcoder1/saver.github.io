package com.example.saver;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebviewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView = findViewById(R.id.web_veiew_id);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        new Handler().postDelayed(() -> webView.loadUrl("http://cohk2.sci-project.lboro.ac.uk/HelpGuide.html"), 200);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            if (webView != null)
                webView.stopLoading();
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        if (webView.canGoBack()) {
            webView.goBack();
            webView.stopLoading();
        }
        super.onPause();
    }
}