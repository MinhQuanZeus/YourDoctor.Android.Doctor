package com.yd.yourdoctorpartnerandroid.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.support.v7.widget.Toolbar;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;
import com.yd.yourdoctorpartnerandroid.DoctorApplication;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.models.New;
import com.yd.yourdoctorpartnerandroid.utils.SocketUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReaderActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.wv_reader)
    WebView webView;
    @BindView(R.id.progressBar)
    ProgressBar progress;
    private New newModel;
    private ArrayList<New> newsFavortite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        ButterKnife.bind(this);
        Socket socket = DoctorApplication.self().getSocket();
        socket.connect();
        progress.setMax(100);
        progress.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            }
        });
        webView.setWebViewClient(new MyBrowser());
        webView.setWebChromeClient(new MyWebViewClient());
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        newModel = (New) bundle.getSerializable("newModel");
        Log.d("Reader", newModel.toString());
        toolbar.setTitle(newModel.title);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(newModel.link);
    }

    public void setValue(int progress) {
        this.progress.setProgress(progress);
    }


    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }


    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            ReaderActivity.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }
}

