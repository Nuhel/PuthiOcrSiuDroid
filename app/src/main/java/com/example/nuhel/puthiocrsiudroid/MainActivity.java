package com.example.nuhel.puthiocrsiudroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView imageView;
    private Button addImage;
    private Button Convert;
    private TextView show;

    private Uri uri;
    private Response response = null;

    private String imagesrc;

    private String result = "";
    private String result_ocr = "";

    private ProgressDialog progressDialog;

    private File upload_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        addImage = (Button) findViewById(R.id.addimage);
        Convert = (Button) findViewById(R.id.convert_button);
        imageView = (ImageView) findViewById(R.id.imageview);
        show = (TextView) findViewById(R.id.show_text);




        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        Convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Featching Ocr");
                progressDialog.setMessage("Loading.....");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                up2();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == MainActivity.this.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }

            Picasso.with(this).load(data.getData()).into(imageView);
            uri = data.getData();


            imagesrc = getRealPathFromURI(uri);

            upload_file = new File(imagesrc);


            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Featching Token");
            progressDialog.setMessage("Loading");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            up();
        }
    }


    private void up2() {


        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {

                OkHttpClient client = new OkHttpClient();
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(80, TimeUnit.SECONDS);
                builder.readTimeout(80, TimeUnit.SECONDS);
                builder.writeTimeout(80, TimeUnit.SECONDS);
                client = builder.build();
                Request request = new Request.Builder()
                        .url("http://113.11.120.208/do_ocr?src=" + result)
                        .get()
                        .addHeader("postman-token", "d7587c37-c06b-3040-36d8-e74d04dad521")
                        .addHeader("cache-control", "no-cache")
                        .build();
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        result_ocr = response.body().string();

                    } else
                        result_ocr = request.body().toString() + "Nuhel2";
                } catch (IOException e) {
                    result_ocr = e.toString() + "Nuhel3";
                    e.printStackTrace();
                }
                return result_ocr;
            }

            @Override
            protected void onPostExecute(String s) {
                show.append(s);
                progressDialog.dismiss();

            }
        }.execute();
    }



    private void up() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {

                OkHttpClient client = new OkHttpClient();
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(80, TimeUnit.SECONDS);
                builder.readTimeout(80, TimeUnit.SECONDS);
                builder.writeTimeout(80, TimeUnit.SECONDS);
                client = builder.build();

                MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");

                RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"sampleFile\"; filename=\"" + upload_file.getName() + "\"\r\nContent-Type: image/jpeg\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
                Request request = new Request.Builder()
                        .url("http://113.11.120.208/upload")
                        .post(body)
                        .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "d7587c37-c06b-3040-36d8-e74d04dad521")
                        .build();




                try {
                    response = client.newCall(request).execute();

                    if(response.isSuccessful()){
                        result = response.body().string();
                    }else{
                        result = response.body().string();
                    }

                } catch (IOException e) {
                    result = e.toString() + "Nuhel3";
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                progressDialog.dismiss();
                show.setText(s + "\n");;
            }
        }.execute();
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
