package com.example.photoapp2.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

 // From http://developer.aiwgame.com/imageview-show-image-from-url-on-android-4-0.html

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    char size;

    public DownloadImageTask(ImageView bmImage, char size) {
        this.bmImage = bmImage;
        this.size = size;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0] + ".jpg";
        if(size != '-')
        {
            urldisplay = urls[0] + "_" + size + ".jpg";
        }

        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
