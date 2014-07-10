package com.example.photoapp2.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Natasha Lotra on 2014/06/24.
 */
public class ShowImageActivity extends Activity implements DownloadImageTask.ImageCallback
{
    private ImageView iv;
    private String url, title = "", id, locality = "", country = "";
    private double lat, lon;
    private Button btnShowMap;
    private ProgressBar progSpin;
    private RelativeLayout imgInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image);

        //get the image info container
        imgInfo = (RelativeLayout) findViewById(R.id.image_info);
        //get the progress spinner
        progSpin = (ProgressBar) findViewById(R.id.progSpinner);

        //get the info required
        Intent i = getIntent();
        this.url = i.getStringExtra("url");
        this.title = i.getStringExtra("title");
        this.id = i.getStringExtra("id");
        Log.d("id", this.id);

        //show the progress spinner
        progSpin.setVisibility(View.VISIBLE);

        //download the image to the image view
        iv = (ImageView) findViewById(R.id.image_view);
        new DownloadImageTask(this, 'b').execute(url);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        //get the map button
        btnShowMap = (Button) findViewById(R.id.btn_showmap);
        new getLocationInfo().execute(id);

        setTitle(title);
    }

    @Override
    public void onImageResult(Bitmap image) {
        //load the imageview
        iv.setImageBitmap(image);

        //stop the progress spinner
        if(progSpin.getVisibility() == View.VISIBLE) {
            progSpin.setVisibility(View.GONE);
        }

        //when the image is clicked, show or hide the image info
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgInfo.getVisibility() == View.VISIBLE) {
                    imgInfo.setVisibility(View.GONE);
                }
                else if(!title.isEmpty()){
                    imgInfo.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void onShowMap(View view)
    {
        Log.d("map", "Show map");

        Intent i = new Intent(getBaseContext(), ShowMapActivity.class);
        i.putExtra("url", this.url);
        i.putExtra("title", this.title);
        i.putExtra("lat", lat);
        i.putExtra("lon", lon);
        startActivity(i);
    }

    private class getLocationInfo extends AsyncTask<String, Void, Boolean>
    {
        private static final String API_KEY = "0b50fdd9304a54276d22994eb20f27a8";

        @Override
        protected Boolean doInBackground(String... id) {
            String apiurl = "https://api.flickr.com/services/rest/?method=flickr.photos.geo.getLocation&api_key=" + this.API_KEY + "&photo_id=" + id[0] + "&format=json";
            Log.d("apiurl", apiurl);
            boolean hasGeo = true;

            try
            {
                URL url = new URL(apiurl);
                URLConnection conn = url.openConnection();
                String line;
                StringBuilder builder = new StringBuilder();
                InputStreamReader is = new InputStreamReader(conn.getInputStream());
                BufferedReader reader = new BufferedReader(is);

                while ((line = reader.readLine()) != null)
                {
                    builder.append(line);
                }

                // response returns invalid json
                Log.d("response", builder.toString());

                // trim the junk
                int start = builder.toString().indexOf("(") + 1;
                int end = builder.toString().length() - 1;
                String response = builder.toString().substring(start,end);

                JSONObject jsonObj = new JSONObject(response); //parse the string to a json object
                Log.d("status", jsonObj.getString("stat"));
                //check if response is okay
                if(jsonObj.getString("stat").equals("ok")) {
                    JSONObject photoLocObj = jsonObj.getJSONObject("photo").getJSONObject("location"); //get the inner object
                    lat = photoLocObj.getDouble("latitude");
                    lon = photoLocObj.getDouble("longitude");
                    if(photoLocObj.has("locality")) {
                        locality = photoLocObj.getJSONObject("locality").getString("_content");
                    }
                    else if(photoLocObj.has("region")) {
                        locality = photoLocObj.getJSONObject("region").getString("_content");
                    }
                    if(photoLocObj.has("country")) {
                        country = photoLocObj.getJSONObject("country").getString("_content");
                    }
                }
                else {
                    hasGeo = false;
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return hasGeo;
        }

        @Override
        protected void onPostExecute(Boolean hasGeo)
        {
            //hide map button if no location info exists
            if(hasGeo) {
                //show map button
                btnShowMap.setVisibility(View.VISIBLE);
                //set onclick listener
                btnShowMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onShowMap(v);
                    }
                });
            }

            TextView tvTitle = (TextView) findViewById(R.id.image_title);
            TextView tvLocation = (TextView) findViewById(R.id.image_location);

            tvTitle.setText(title);
            if(locality != "") {
                tvLocation.setText(locality + ", " + country);
            }
            else {
                tvLocation.setText(country);
            }

            Log.d("photoInfo", "Title: " + title + "HasGeo: " + hasGeo);

            //if no image information exists, hide the info fragment
            if(title.isEmpty() && !hasGeo) {
                imgInfo.setVisibility(View.GONE);
                Log.d("hideInfo", "No image info available");
            }
        }
    }
}
