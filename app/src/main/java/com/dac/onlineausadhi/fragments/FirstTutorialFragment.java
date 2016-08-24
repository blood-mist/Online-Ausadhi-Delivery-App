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
public class FirstTutorialFragment extends Fragment {
    TextView noPharmacy;
    TextView fastFree;
    TextView doorstep;
    TextView saveTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_frag, container, false);
        Typeface mTypeface = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);
        noPharmacy = (TextView) view.findViewById(R.id.no_pharmacy);
        fastFree = (TextView) view.findViewById(R.id.fast_free);
        doorstep = (TextView) view.findViewById(R.id.doorstep);
        saveTime = (TextView) view.findViewById(R.id.image1);


        noPharmacy.setTypeface(mTypeface);
        fastFree.setTypeface(mTypeface);
        doorstep.setTypeface(mTypeface);
        saveTime.setTypeface(mTypeface);
        saveTime.setTextSize(100);
        return view;
    }

    public static FirstTutorialFragment newInstance() {

        FirstTutorialFragment fragmentFirst = new FirstTutorialFragment();
        Bundle args = new Bundle();
        fragmentFirst.setArguments(args);

        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
