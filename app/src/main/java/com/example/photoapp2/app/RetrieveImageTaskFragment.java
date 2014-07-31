package com.example.photoapp2.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Natasha Lotra on 2014/06/24.
 */
public class RetrieveImageTaskFragment extends Fragment
{
    private TaskCallbacks callbacks;
    private String searchTerm = "";
    private String latitude = "0";
    private String longitude = "0";

    static interface TaskCallbacks
    {
        void onPreExecute();
        void onProgressUpdate(int percent);
        void onCancelled();
        void onPostExecute(ArrayList <Photo> photos);
    }

    public RetrieveImageTaskFragment(String searchTerm)
    {
        this.searchTerm = searchTerm;
    }

    public RetrieveImageTaskFragment()
    {

    }

    public RetrieveImageTaskFragment(String lat, String lon)
    {
        this.latitude = lat;
        this.longitude = lon;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //retain fragment
        setRetainInstance(true);

        Log.d("retaindfragment", "Retained fragment loaded");

        //if searchterm is null, send co-ordinates to asynctask
        if(searchTerm != "")
        {
            //execute background task
            new RetrieveImagesTask().execute(searchTerm);
        }
        else
        {
            new RetrieveImagesTask().execute(latitude, longitude);
        }
    }

    private class RetrieveImagesTask extends AsyncTask<String, Integer, ArrayList <Photo>>
    {
        private static final String API_KEY = "0b50fdd9304a54276d22994eb20f27a8";
        private ArrayList <Photo> photoArrList = new ArrayList <Photo>();
        private String searchUrl;

        @Override
        protected ArrayList doInBackground(String... params) {
            if(params.length == 1)
            {
                // construct the api url for search terms
                searchUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search&text=" + params[0] + "&api_key=" + API_KEY + "&per_page=50&format=json";
            }
            else
            {
                // construct the api url for location
                searchUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search&lat=" + params[0] + "&lon=" + params[1] + "&api_key=" + API_KEY + "&per_page=50&format=json";
            }

            Log.d("searchurl", "url: " + searchUrl);

            // attempt to retrieve a json result
            try
            {
                URL url = new URL(searchUrl);
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
                if(jsonObj.getString("stat").equals("ok")) {
                    JSONObject jsonObjInner = jsonObj.getJSONObject("photos"); //get the inner object
                    JSONArray photoArr = jsonObjInner.getJSONArray("photo"); //get the array of photos
                    JSONObject photo;

                    // get the photo info for each result returned by the api
                    for (int i = 0; i < photoArr.length(); i++) {
                        photo = photoArr.getJSONObject(i);
                        photoArrList.add(new Photo(constructImageUrl(photo)));
                        photoArrList.get(i).setId(photo.getString("id"));
                        photoArrList.get(i).setOwner(photo.getString("owner"));
                        photoArrList.get(i).setSecret(photo.getString("secret"));
                        photoArrList.get(i).setServer(photo.getString("server"));
                        photoArrList.get(i).setFarm(photo.getString("farm"));
                        photoArrList.get(i).setTitle(photo.getString("title"));
                    }
                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return photoArrList;
        }

        // construct the url for each image
        private String constructImageUrl(JSONObject photoObj) throws JSONException {
            String farm = photoObj.getString("farm");
            String server = photoObj.getString("server");
            String secret = photoObj.getString("secret");
            String id = photoObj.getString("id");

            StringBuilder imgurl = new StringBuilder();

            imgurl.append("http://farm");
            imgurl.append(farm);
            imgurl.append(".staticflickr.com/");
            imgurl.append(server);
            imgurl.append("/");
            imgurl.append(id);
            imgurl.append("_");
            imgurl.append(secret);

            return imgurl.toString();
        }

        @Override
        protected void onPreExecute()
        {
            if(callbacks != null)
            {
                callbacks.onPreExecute();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... percent)
        {
            if(callbacks != null)
            {
                callbacks.onProgressUpdate(percent[0]);
            }
        }

        @Override
        protected void onCancelled()
        {
            if(callbacks != null)
            {
                callbacks.onCancelled();
            }
        }

        @Override
        protected void onPostExecute(ArrayList <Photo> photos)
        {
            Log.d("onpostexecute", "onPostExecute called");
            if(callbacks != null)
            {
                callbacks.onPostExecute(photos);
                Log.d("callbacks", "urls sent");
            }
        }
    }

    public void setCallbacks(TaskCallbacks tc)
    {
        Log.d("callbacks", "set callbacks");
        callbacks = tc;
    }
}
