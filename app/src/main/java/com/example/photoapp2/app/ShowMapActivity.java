package com.example.photoapp2.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Guest on 2014/07/01.
 */
public class ShowMapActivity extends Activity implements DownloadImageTask.ImageCallback {

    private GoogleMap googlemap;
    double lat = 0, lon = 0;
    String url = "", title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);

        Intent i = getIntent();
        this.url = i.getStringExtra("url");
        this.title = i.getStringExtra("title");
        this.lat = i.getDoubleExtra("lat", 0);
        this.lon = i.getDoubleExtra("lon", 0);

        try{
            //loading map
            initialiseMap();
            DownloadImageTask dlImage = new DownloadImageTask(this, 's');
            dlImage.execute(url);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //function to load map. If map is not created, it will create it
    private void initialiseMap(){
        if(googlemap == null){
            googlemap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            //check if map is created successfully
            if(googlemap == null){
                Toast.makeText(getBaseContext(), "Unable to create maps", Toast.LENGTH_SHORT).show();
            }
            else
            {
                googlemap.setMyLocationEnabled(true);

                //move to location
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(lat, lon)).zoom(12).build();
                googlemap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        DownloadImageTask dlImage = new DownloadImageTask(this, 's');
        dlImage.execute(url);
        initialiseMap();
    }

    @Override
    public void onImageResult(Bitmap image) {
        Log.d("location", "lat: " + lat + " lon: " + lon);

        //create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(title);
        marker.icon(BitmapDescriptorFactory.fromBitmap(image));

        //add the marker
        googlemap.addMarker(marker);
    }
}
