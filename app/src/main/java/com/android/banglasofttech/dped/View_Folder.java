package com.android.banglasofttech.dped;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class View_Folder extends AppCompatActivity {

    ArrayList<String> folderTitle, folderID,fileTitle, fileID, fileExtension;
    ProgressBar progress;
    String Link;
    TextView coming;
    LinearLayout content;
    Context context=View_Folder.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__folder);

        folderTitle=new ArrayList<String>();
        folderID=new ArrayList<String>();
        fileTitle=new ArrayList<String>();
        fileID=new ArrayList<String>();

        coming=(TextView)findViewById(R.id.coming);

        content=(LinearLayout)findViewById(R.id.data);
        progress=(ProgressBar)findViewById(R.id.progressBar2);

        if(networkchaeck())new JSONClass().execute();

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
            coming.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            Link=setData.FolderLink+setData.id;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(Link);
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
                JSONObject root = parent.getJSONObject(0);
                JSONArray folder = root.getJSONArray("Folders");

                for (int i = 0; i < folder.length(); i++) {
                    JSONObject chield = folder.getJSONObject(i);
                    folderTitle.add(chield.getString("folder"));
                    folderID.add(chield.getString("folderID"));
                }

                JSONArray file = root.getJSONArray("Files");
                fileExtension=new ArrayList<String>();
                for (int i = 0; i < file.length(); i++) {
                    JSONObject chield = file.getJSONObject(i);
                    fileTitle.add(chield.getString("file"));
                    fileID.add(chield.getString("fileID"));
                    fileExtension.add(chield.getString("extension"));
                }

                setData.root=root.getString("Root");
                setData.rootname=root.getString("Rootname");

                return "1";

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("1")) {
                for (int i = 0; i < folderID.size(); i++) setFolder(i);
                for (int i = 0; i < fileID.size(); i++) setFile(i);

                if(folderID.size()==0 && fileID.size()==0) coming.setVisibility(View.VISIBLE);
                else coming.setVisibility(View.GONE);

                content.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
            else {
                AlertDialog.Builder ad = new AlertDialog.Builder(context);
                ad.setTitle("Sorry !!");
                ad.setCancelable(false);
                ad.setMessage("Something went wrong. Please try again... ");
                ad.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (networkchaeck()) new JSONClass().execute();
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
        TextView tit = (TextView) v.findViewById(R.id.textView5);
        ImageView icon = (ImageView) v.findViewById(R.id.imageView7);

        tit.setTypeface(Typeface.createFromAsset(context.getAssets(), "SabrenaTonnyMJ.TTF"));
        tit.setText(folderTitle.get(position));
        icon.setImageDrawable(getResources().getDrawable(R.drawable.folder3));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setData.id=folderID.get(position);
                setData.location++;
                setData.name=folderTitle.get(position);
                clearContent();
                if(networkchaeck())new JSONClass().execute();
            }
        });

        if(layout.getParent()!=null){
            ((ViewGroup)layout.getParent()).removeView(layout);
        }

        content.addView(layout);
    }

    public void setFile(final int position) {
        View v = getLayoutInflater().inflate(R.layout.itemlist, null);

        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.list);
        TextView tit = (TextView) v.findViewById(R.id.textView5);
        ImageView icon = (ImageView) v.findViewById(R.id.imageView7);

        tit.setTypeface(Typeface.createFromAsset(context.getAssets(), "SabrenaTonnyMJ.TTF"));
//        tit.setText(fileExtension.get(position));
        tit.setText(fileTitle.get(position));
        icon.setImageDrawable(getResources().getDrawable(R.drawable.file2));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setData.name=(fileTitle.get(position));
                setData.file_id=fileID.get(position);
                setData.file_type=fileExtension.get(position);
                if(networkchaeck()) context.startActivity(new Intent(context, view_document.class));

            }
        });

        if(layout.getParent()!=null){
            ((ViewGroup)layout.getParent()).removeView(layout);
        }

        content.addView(layout);
    }

    public void clearContent(){
        content.removeAllViews();
        if(folderID.size()>0)folderID.clear();
        if(folderTitle.size()>0)folderTitle.clear();
        if(fileID.size()>0)fileID.clear();
        if(fileTitle.size()>0) fileTitle.clear();
    }

    @Override
    public void onBackPressed() {
        if(setData.location>1){
            clearContent();
            setData.id=setData.root;
            setData.name=setData.rootname;
            setData.location--;
            if (networkchaeck()) new JSONClass().execute();
        }
        else super.onBackPressed();
    }

    public void ad(){
        AdView adview=  (AdView)findViewById(R.id.adView);
        AdRequest adRequest=new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
        adview.loadAd(adRequest);
    }

}
