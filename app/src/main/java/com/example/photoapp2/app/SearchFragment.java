package com.example.photoapp2.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

/**
 * Created by Natasha Lotra on 2014/06/24.
 */
public class SearchFragment extends Fragment implements RetrieveImageTaskFragment.TaskCallbacks, DownloadImageTask.ImageCallback
{
    private static final String RETRIEVE_IMAGE_TASK = "retrieve_search_images";
    private RetrieveImageTaskFragment retrieveImageFragment;
    private View view;
    private TableLayout tblLayout;
    private ArrayList <Photo> photoInfo;
    private ArrayList <ImageView> imageView;
    private int loadcount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //inflate the layout
        view = inflater.inflate(R.layout.search_fragment, container, false);

        //set onclicklistener
        Button btnSearch = (Button) view.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                onSearch(v);
            }
        });

        //get the table layout
        tblLayout = (TableLayout) view.findViewById(R.id.image_grid);

        retrieveImageFragment = (RetrieveImageTaskFragment) getFragmentManager().findFragmentByTag(RETRIEVE_IMAGE_TASK);
        // if the fragment is not null, it is being retained
        if(retrieveImageFragment != null)
        {
            retrieveImageFragment.setCallbacks(this);
        }

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

        FragmentManager fm = getFragmentManager();

        EditText etSearch = (EditText) this.view.findViewById(R.id.et_search);
        String searchTerm = etSearch.getText().toString().trim();

        if(searchTerm != "")
        {
            tblLayout.removeAllViews();
            retrieveImageFragment = new RetrieveImageTaskFragment(searchTerm);
            retrieveImageFragment.setCallbacks(this);
            fm.beginTransaction().add(retrieveImageFragment, RETRIEVE_IMAGE_TASK).commit();
        }
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
    public void onPostExecute(ArrayList <Photo> photos){
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
        imageView = new ArrayList<ImageView>();

        int count = 0;
        int rowCount = 0;
        int size = 154;
        loadcount = 0;

        // load the images into a table
        while(count < photos.size())
        {
            tblRows.add(new TableRow(getActivity().getBaseContext()));

            for(int y = 0; y < 3; y++)
            {
                if(count < photos.size() && photos.get(count) != null)
                {
                    this.imageView.add(new ImageView(getActivity().getBaseContext()));

                    // download the image
                    new DownloadImageTask(this, 'q').execute(photos.get(count).getUrl());
                    imageView.get(count).setPadding(2, 2, 2, 2);
                    imageView.get(count).setMaxHeight(size);
                    imageView.get(count).setMaxWidth(size);
                    imageView.get(count).setMinimumHeight(size);
                    imageView.get(count).setMinimumWidth(size);
                    imageView.get(count).setId(count);

                    //add the views to the row
                    tblRows.get(rowCount).addView(imageView.get(count));
                    imageView.get(count).setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onImageResult(Bitmap image) {
        imageView.get(loadcount).setImageBitmap(image);
        loadcount++;
    }
}
