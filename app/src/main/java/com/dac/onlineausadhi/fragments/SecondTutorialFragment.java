package com.dac.onlineausadhi.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dac.onlineausadhi.classes.FontManager;
import com.dac.onlineausadhi.onlineaushadhilin.R;

/**
 * Created by blood-mist on 5/17/16.
 */
public class SecondTutorialFragment extends Fragment {

    TextView care;
    TextView highQuality;
    TextView confidential;
    TextView trustworthy;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.second_frag, container, false);
        Typeface mTypeface = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);
        care= (TextView) view.findViewById(R.id.care);
        highQuality= (TextView) view.findViewById(R.id.high_quality);
        confidential= (TextView) view.findViewById(R.id.confidential);
        trustworthy= (TextView) view.findViewById(R.id.image2);

        care.setTypeface(mTypeface);
        highQuality.setTypeface(mTypeface);
        confidential.setTypeface(mTypeface);
        trustworthy.setTypeface(mTypeface);
        trustworthy.setTextSize(100);


        return view;
    }

    public static SecondTutorialFragment newInstance(String s) {

        SecondTutorialFragment fragmentSecond = new SecondTutorialFragment();
        Bundle args = new Bundle();
        fragmentSecond.setArguments(args);

        return fragmentSecond;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
