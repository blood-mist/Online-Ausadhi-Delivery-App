package com.dac.onlineausadhi.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by blood-mist on 7/1/16.
 */
public final class PlaceSlideFragment extends Fragment {
    int imageResourceId;

    public static PlaceSlideFragment newInstance(int i) {
        PlaceSlideFragment fragment=new PlaceSlideFragment();
        Bundle bundle = new Bundle(2);
        bundle.putInt("imageResourceId",i);
        fragment.setArguments(bundle);
        return fragment ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        imageResourceId=getArguments().getInt("imageResourceId");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ImageView image = new ImageView(getActivity());
        image.setImageResource(imageResourceId);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new ViewPager.LayoutParams());

        layout.setGravity(Gravity.CENTER);
        layout.addView(image);

        return layout;
    }
}
