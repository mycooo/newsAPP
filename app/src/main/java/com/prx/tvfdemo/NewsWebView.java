package com.prx.tvfdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class NewsWebView extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(
                "http://docs.google.com/gview?embedded=true&url=uet.vnu.edu.vn/~chauttm/e-books/java/Effective.Java.2nd.Edition.May.2008.3000th.Release.pdf");
    }

}

