package com.futurice.festapp.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.futurice.festapp.android.R;

import uk.co.senab.photoview.PhotoViewAttacher;

public class VenueFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final Bundle bundle = getArguments();
        View view = inflater.inflate(R.layout.fragment_venue, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.map);
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
        photoViewAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return view;
    }
}
