package com.example.photoapp2.app;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Natasha Lotra on 2014/06/24.
 */
public class RetrieveImageTaskFragment extends Fragment
{
    private TaskCallbacks callbacks;


    static interface TaskCallbacks
    {
        void onPreExecute();
        void onProgressUpdate(int percent);
        void onCancelled();
        void onPostExecute(Photo[] urls);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //retain fragment
        setRetainInstance(true);

        Log.d("retaindfragment", "Retained fragment loaded");

        //execute background task
        new RetrieveImagesTask().execute("cats");
    }

    private class RetrieveImagesTask extends AsyncTask<String, Integer, Photo[]>
    {
        private static final String API_KEY = "0b50fdd9304a54276d22994eb20f27a8";
        private Photo[] photoUrl;

        @Override
        protected Photo[] doInBackground(String... searchString) {
            // construct the api url
            String stringUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search&text=" + searchString[0] + "&api_key=" + API_KEY + "&per_page=9&format=json";

            Log.d("constrcturl", "url: " + stringUrl);

            // attempt to retrieve a json result
            try
            {
                URL url = new URL(stringUrl);
                URLConnection conn = url.openConnection();
                String line;
                StringBuilder builder = new StringBuilder();
                InputStreamReader is = new InputStreamReader(conn.getInputStream());
                BufferedReader reader = new BufferedReader(is);

                while ((line = reader.readLine()) != null)
                {
                    builder.append(line);
                }

                //response returns invalid json
                Log.d("response", builder.toString());

                //trim the junk
                int start = builder.toString().indexOf("(") + 1;
                int end = builder.toString().length() - 1;
                String response = builder.toString().substring(start,end);

                JSONObject jsonObj = new JSONObject(response); //parse the string to a json object
                JSONObject jsonObjInner = jsonObj.getJSONObject("photos"); //get the inner object
                JSONArray photoArr = jsonObjInner.getJSONArray("photo"); //get the array of photos
                JSONObject photo;

                photoUrl = new Photo[photoArr.length()];

                // get the photo urls for each result returned from the api
                for(int i = 0; i < photoArr.length(); i++)
                {
                    photo = photoArr.getJSONObject(i);
                    photoUrl[i] = new Photo(constructImageUrl(photo));
                    Log.d("photourl", photoUrl[i].getUrl());
                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return photoUrl;
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
            imgurl.append(".jpg");

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
        protected void onPostExecute(Photo[] urls)
        {
            Log.d("onpostexecute", "onPostExecute called");
            if(callbacks != null)
            {
                callbacks.onPostExecute(urls);
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
