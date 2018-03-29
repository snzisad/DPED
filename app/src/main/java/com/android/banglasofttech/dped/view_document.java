package com.android.banglasofttech.dped;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class view_document extends AppCompatActivity {

    ProgressBar progress;
    String downloadLink,viewlink;
    WebView wv;
    Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document);

        super.setTitle(setData.name);

        progress=(ProgressBar)findViewById(R.id.progressBar5);

        downloadLink=makeDownloadLink();
        viewlink=makeViewLink();

        wv = (WebView) findViewById(R.id.webview);
        WebSettings w = wv.getSettings();
        w.setJavaScriptEnabled(true);

        if(networkchaeck()){
            wv.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progress.setVisibility(View.VISIBLE);
                    wv.setVisibility(View.GONE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progress.setVisibility(View.GONE);
                    wv.setVisibility(View.VISIBLE);
                }
            });
            wv.loadUrl(setData.viewerLink + viewlink);
        }

        ad();
    }

    public boolean networkchaeck() {
        super.setTitle(setData.name);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)) {
            AlertDialog.Builder ad = new AlertDialog.Builder(context);
            ad.setTitle("Connect to Internet");
            ad.setCancelable(false);
            ad.setMessage("Please check your internet connection and try again.");
            ad.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(networkchaeck())wv.loadUrl(setData.viewerLink + viewlink);
                }
            });
            ad.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            ad.create();
            ad.show();
            return false;
        }
        return true;
    }

    public String makeViewLink(){
        String s;
        s=setData.downloadLink+setData.file_id+".pdf";
        return s;
    }

    public String makeDownloadLink(){
        String s;
        s=setData.downloadLink+setData.file_id+"."+setData.file_type;
        return s;
    }

    public void ad(){
        AdView adview=  (AdView)findViewById(R.id.adView);
        AdRequest adRequest=new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
        adview.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.download,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.download_menu){
            Intent downloadfile = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadLink));
            startActivity(downloadfile);
        }

        return super.onOptionsItemSelected(item);
    }

}
