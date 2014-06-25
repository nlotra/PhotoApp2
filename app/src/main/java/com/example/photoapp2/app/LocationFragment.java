package com.example.photoapp2.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Natasha Lotra on 2014/06/24.
 */
public class LocationFragment extends Fragment
{
    @Override
     public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //inflate the layout
        View view = inflater.inflate(R.layout.location_fragment, container, false);
        return view;
    }
}
