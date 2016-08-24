package com.dac.onlineausadhi.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dac.onlineausadhi.classes.FontManager;
import com.dac.onlineausadhi.onlineaushadhilin.R;

/**
 * Created by blood-mist on 6/24/16.
 */
public class Slider extends Fragment {
    TextView noPharmacy;
    TextView fastFree;
    TextView doorstep;
    TextView saveTime;

    TextView care;
    TextView highQuality;
    TextView confidential;
    TextView trustworthy;

    TextView reminder;
    TextView resend;
    TextView worry;
    TextView refill;



    private static final String ARG_LAYOUT_RES_ID = "layoutResId";

        public static Slider newInstance(int layoutResId) {
            Slider sampleSlide = new Slider();

            Bundle args = new Bundle();
            args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
            sampleSlide.setArguments(args);

            return sampleSlide;
        }

        private int layoutResId;

        public Slider() {}

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
                layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(layoutResId, container, false);
            Typeface mTypeface = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);

            switch (layoutResId ) {
                case R.layout.first_frag:
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
                case R.layout.second_frag:
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
                case R.layout.third_frag:
                    reminder= (TextView) view.findViewById(R.id.reminder);
                    resend= (TextView) view.findViewById(R.id.resend);
                    worry= (TextView) view.findViewById(R.id.no_worry);
                    refill= (TextView) view.findViewById(R.id.image3);

                    reminder.setTypeface(mTypeface);
                    resend.setTypeface(mTypeface);
                    worry.setTypeface(mTypeface);
                    refill.setTypeface(mTypeface);
                    refill.setTextSize(100);
                    return view;


            }
            return view;
        }
}
