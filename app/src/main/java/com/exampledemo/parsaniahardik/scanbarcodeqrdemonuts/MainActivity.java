package com.exampledemo.parsaniahardik.scanbarcodeqrdemonuts;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    List<String> arrayList;
    String whitelistedURL = "http://appraise.000webhostapp.com/whitelisted.txt";
    private String strDomain = "http://appraise.000webhostapp.com/";
    public static TextView tvresult;
    public static String url = "";
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        webView = new WebView(getApplicationContext());
        tvresult = (TextView) findViewById(R.id.tvresult);

        ListView listView = null;
        Button         btnScan = (Button) findViewById(R.id.btnSCAN);
        Button         btnReload = (Button) findViewById(R.id.btnReload);
        Button         btnVisit = (Button) findViewById(R.id.btnVISIT);
        Button         btnGenerateBarcode = (Button) findViewById(R.id.btnGenerateBarcode);

        btnReload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                populateListView();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });
        btnVisit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(isNetworkAvailable())
                {
                    if(!url.contains("here"))
                    {
                        openWebView(url);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "WebView was not started, no internet connection or error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnGenerateBarcode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog();
            }
        });
        try
        {
            arrayList = new ArrayList<String>();
            listView = (ListView) findViewById(R.id.listView);

            if(isNetworkAvailable())
            {
                try
                {
                    populateListView();

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            String current = arrayList.get(position);
                            System.out.println("Clicked: " + current);
                            if(isNetworkAvailable())
                            {
                                openWebView(strDomain + current);
                            }
                            else
                            {
                                showNoConnectionToast();
                            }
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(this,"Failed loading whitelisted elements",Toast.LENGTH_SHORT).show();
                }

            }
            else
            {
                showNoConnectionToast();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void populateListView()
    {
        try
        {

            if(isNetworkAvailable())
            {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

                ListView listView = (ListView) findViewById(R.id.listView);

                listView.setAdapter(arrayAdapter);
                arrayList = new ArrayList<>();
                ReadWebPagex.readURL(arrayList, whitelistedURL);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void showNoConnectionToast()
    {
        Toast.makeText(this, "No internet connection or error", Toast.LENGTH_SHORT).show();
    }
    private void openWebView(String url)
    {
        webView.setWebViewClient(new WebViewClient());
        //webView.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = webView.getSettings();
        //webSettings.setJavaScriptEnabled(true);
        //webSettings.setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.loadUrl(url);
        setContentView(webView);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void showDialog()
    {
        final Dialog dialog = new Dialog(this);

        View view = getLayoutInflater().inflate(R.layout.list_view_dialog_layout, null);
        ListView lv = (ListView) view.findViewById(R.id.listViewDialog);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayList);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String clickedHTML = arrayList.get(position);
                System.out.println("Item clicked: " + clickedHTML);

                dialog.dismiss();
                try
                {
                    Bitmap    bitmap = TextToImageEncode(strDomain+clickedHTML);
                    final Dialog dialog = new Dialog(parent.getContext());

                    View mainView = getLayoutInflater().inflate(R.layout.simple_drawable_dialog, null);
                    ImageView imv = (ImageView) mainView.findViewById(R.id.imageViewQR);
                    imv.setImageBitmap(bitmap);

                    ( (LinearLayout) mainView).removeAllViews();
                    dialog.setContentView(imv);
                    dialog.show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        dialog.setContentView(view);
        dialog.show();

    }
    public String saveImage(Bitmap myBitmap)
    {
        String IMAGE_DIRECTORY = ""; //alex añadido
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d("dirrrrrr", "" + wallpaperDirectory.mkdirs());
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();   //give read write permission
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";

    }
    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            //Value = "www.google.es"; // testing
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    250, 250, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                                     getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 250, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
