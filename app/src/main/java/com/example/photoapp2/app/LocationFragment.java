package com.example.photoapp2.app;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Natasha Lotra on 2014/06/24.
 */
public class LocationFragment extends Fragment implements LocationListener, RetrieveImageTaskFragment.TaskCallbacks
{
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private View view;

    private static final String RETRIEVE_IMAGE_TASK = "retrieve_location_images";
    private RetrieveImageTaskFragment retrieveImageFragment;
    private TableLayout tblLayout;
    private ArrayList<Photo> photoInfo;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // get location manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // get the best provider (use default criteria)
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);

        if(location != null)
        {
            onLocationChanged(location);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // inflate the layout
        view = inflater.inflate(R.layout.location_fragment, container, false);

        //get the table layout
        tblLayout = (TableLayout) view.findViewById(R.id.image_grid);

        //set onclicklistener
        Button btnSearch = (Button) view.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                onSearch(v);
            }
        });

        return view;
    }

    @Override
    public void onDestroy()
    {
        retrieveImageFragment.setCallbacks(null);
    }

    public void onSearch(View view)
    {
        Log.d("search", "search clicked");

        location = locationManager.getLastKnownLocation(provider);
        FragmentManager fm = getFragmentManager();

        if(location != null)
        {
            String lat = String.valueOf(location.getLatitude());
            String lon = String.valueOf(location.getLongitude());
            Log.d("location", "lat: " + lat + " lon: " + lon);

            tblLayout.removeAllViews();
            retrieveImageFragment = new RetrieveImageTaskFragment(lat, lon);
            retrieveImageFragment.setCallbacks(this);
            fm.beginTransaction().add(retrieveImageFragment, RETRIEVE_IMAGE_TASK).commit();
        }
        else
        {
            Log.d("location", "no location detected");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // request location updates when resumed
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        // remove location updates when paused
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        Log.d("location", "lat: " + lat + " lon: " + lon);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        // ignore
    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(getActivity().getBaseContext(), "Provider " + provider + " enabled", Toast.LENGTH_SHORT);
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(getActivity().getBaseContext(), "Provider " + provider + " disabled", Toast.LENGTH_SHORT);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(ArrayList<Photo> photos) {
        for(int i = 0; i < photos.size(); i++)
        {
            Log.d("photourl", photos.get(i).getUrl());
            if(photos.get(i).getTitle() != null)
            {
                Log.d("phototitle", photos.get(i).getTitle());
            }
        }

        // Create the layout
        ArrayList <TableRow> tblRows = new ArrayList<TableRow>();

        int count = 0;
        int rowCount = 0;
        int size = 154;

        // load the images into a table
        while(count < photos.size())
        {
            tblRows.add(new TableRow(getActivity().getBaseContext()));

            for(int y = 0; y < 3; y++)
            {
                if(count < photos.size() && photos.get(count) != null)
                {
                    ImageView image = new ImageView(getActivity().getBaseContext());

                    // download the image
                    new DownloadImageTask(image, 'q').execute(photos.get(count).getUrl());
                    image.setPadding(2, 2, 2, 2);
                    image.setMaxHeight(size);
                    image.setMaxWidth(size);
                    image.setMinimumHeight(size);
                    image.setMinimumWidth(size);
                    image.setId(count);

                    //add the views to the row
                    tblRows.get(rowCount).addView(image);
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onImageClick(v);
                        }
                    });

                    count++;
                }
            }
            //add the rows to the table
            tblLayout.addView(tblRows.get(rowCount));
            rowCount++;
        }

        photoInfo = photos;
    }

    public void onImageClick(View view)
    {
        Log.d("Image Click", "Image clicked: " + view.getId());

        Intent i = new Intent(getActivity().getBaseContext(), ShowImageActivity.class);
        i.putExtra("id", photoInfo.get(view.getId()).getId());
        i.putExtra("owner", photoInfo.get(view.getId()).getOwner());
        i.putExtra("secret", photoInfo.get(view.getId()).getSecret());
        i.putExtra("server", photoInfo.get(view.getId()).getServer());
        i.putExtra("farm", photoInfo.get(view.getId()).getFarm());
        i.putExtra("title", photoInfo.get(view.getId()).getTitle());
        i.putExtra("url", photoInfo.get(view.getId()).getUrl());
        startActivity(i);
    }
}
