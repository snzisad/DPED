package com.android.banglasofttech.dped;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> title, id;
    ProgressBar progress;
    LinearLayout home;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        super.setTitle("ডিপিএড হোম");

        title=new ArrayList<String>();
        id=new ArrayList<String>();

        setData.context=MainActivity.this;

        home=(LinearLayout)findViewById(R.id.home);
        progress=(ProgressBar)findViewById(R.id.progressBar);

        if(networkchaeck())new JSONClass().execute(setData.HomeLink);

        ad();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.about_menu){
            startActivity(new Intent(this, About.class));
//            Toast.makeText(this, "This is About Page", Toast.LENGTH_SHORT).show();
        }
        else if (id==R.id.share_menu){
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBodyText = "Hi dear, Download DPED application from the following link and enjoy DPED training at home. Download now: https://play.google.com/store/apps/details?id=com.android.banglasofttech.dped";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Download DPED");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
            startActivity(Intent.createChooser(sharingIntent, "Share DPED"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean networkchaeck() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)) {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Connect to Internet");
            ad.setCancelable(false);
            ad.setMessage("Please check your internet connection and try again.");
            ad.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(networkchaeck())new JSONClass().execute(setData.HomeLink);
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

    public class JSONClass extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            home.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line, data;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                data = buffer.toString();
                connection.disconnect();

                JSONArray parent = new JSONArray(data);
                JSONObject folder = parent.getJSONObject(0);
                JSONArray root = folder.getJSONArray("Data");

                for (int i = 0; i < root.length(); i++) {
                    JSONObject chield = root.getJSONObject(i);
                    title.add(chield.getString("folder"));
                    id.add(chield.getString("folderID"));
                }

                return "1";

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("1")){
                for(int i=0;i<title.size();i++) setFolder(i);
                home.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
            else {
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setTitle("Sorry !!");
                ad.setMessage("Something went wrong. Please try again");
                ad.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (networkchaeck()) new JSONClass().execute(setData.HomeLink);
                    }
                });
                ad.create();
                ad.show();

            }

        }
    }

    public void setFolder(final int position) {
        View v = getLayoutInflater().inflate(R.layout.itemlist, null);

        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.list);
        final TextView tit = (TextView) v.findViewById(R.id.textView5);
        ImageView icon = (ImageView) v.findViewById(R.id.imageView7);

        tit.setTypeface(Typeface.createFromAsset(this.getAssets(), "SabrenaTonnyMJ.TTF"));
        tit.setText(title.get(position));
        icon.setImageDrawable(getResources().getDrawable(R.drawable.folder3));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setData.id=id.get(position);
                setData.location=1;
                setData.name=title.get(position);
                startActivity(new Intent(MainActivity.this, View_Folder.class));
            }
        });

        if(layout.getParent()!=null){
            ((ViewGroup)layout.getParent()).removeView(layout);
        }

        home.addView(layout);
    }

    public void ad(){
        AdView adview=  (AdView)findViewById(R.id.adView);
        AdRequest adRequest=new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
        adview.loadAd(adRequest);
    }
}
