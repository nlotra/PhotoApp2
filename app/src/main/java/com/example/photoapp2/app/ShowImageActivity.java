package com.example.photoapp2.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by Natasha Lotra on 2014/06/24.
 */
public class ShowImageActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image);

        //get the info required
        Intent i = getIntent();
        String url = i.getStringExtra("url");
        String title = i.getStringExtra("title");

        //download the image to the image view
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        new DownloadImageTask(iv, 'b').execute(url);

        setTitle(title);
    }
}
