package com.example.photoapp2.app;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Natasha Lotra on 2014/06/24.
 */
public class SearchFragment extends Fragment implements RetrieveImageTaskFragment.TaskCallbacks
{
    private static final String RETRIEVE_IMAGE_TASK = "retrieve_images";
    private RetrieveImageTaskFragment retrieveImageFragment;
    private View view;

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
            retrieveImageFragment = new RetrieveImageTaskFragment(searchTerm);
            retrieveImageFragment.setCallbacks(this);
            fm.beginTransaction().add(retrieveImageFragment, RETRIEVE_IMAGE_TASK).commit();
        }

//        TableRow[] tblRows = new TableRow[9];
//
//
//
//        // Create the layout
//        TableLayout tblLayout = (TableLayout) getActivity().findViewById(R.id.imageGrid);
//
//        // load the images into a table
//        for(int x = 0; x < 3; x++)
//        {
//            tblRows[x] = new TableRow(getActivity().getBaseContext());
//
//            for(int y = 0; y < 3; y++)
//            {
//
////                if(random.nextInt(3) == 0 && numGiven > 0)
////                {
////                    TextView curr = new TextView(this);
////                    curr.setWidth(50);
////                    curr.setHeight(50);
////                    curr.setBackgroundResource(R.drawable.border);
////                    curr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
////                    curr.setGravity(Gravity.CENTER);
////                    curr.setTextColor(Color.GRAY);
////                    curr.setText("" + numbers[x][y]);
////                    numViews[x][y] = curr;
////                    given[x][y] = true;
////                    numGiven--;
////                }
////                else
////                {
////                    EditText curr = new EditText(this);
////                    curr.setWidth(50);
////                    curr.setHeight(50);
////                    curr.setBackgroundResource(R.drawable.border);
////                    curr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
////                    curr.setGravity(Gravity.CENTER);
////                    curr.setFilters(filter);
////                    curr.setKeyListener(digitlistener);
////                    given[x][y] = false;
////                    numViews[x][y] = curr;
////                }
////                //add the views to the row
////                tblRows[x].addView(numViews[x][y]);
//            }
//            //add the rows to the table
//            tblLayout.addView(tblRows[x]);
//        }
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
    public void onPostExecute(Photo[] photos){
        for(int i = 0; i < photos.length; i++)
        {
            Log.d("callbackurl", photos[i].getUrl());
        }
    }
}
