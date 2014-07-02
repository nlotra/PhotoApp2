package com.example.photoapp2.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    char size;

    private ImageCallback callback;

    static interface ImageCallback
    {
        void onImageResult(Bitmap image);
    }

    public DownloadImageTask(ImageCallback callback, char size) {
        this.callback = callback;
        this.size = size;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0] + ".jpg";
        if(size != '-')
        {
            urldisplay = urls[0] + "_" + size + ".jpg";
        }

        Bitmap imageBitmap = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            imageBitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return imageBitmap;
    }

    protected void onPostExecute(Bitmap result) {
        callback.onImageResult(result);
    }
}
