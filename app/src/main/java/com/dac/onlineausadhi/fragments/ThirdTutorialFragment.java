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
public class ThirdTutorialFragment extends Fragment{
    TextView reminder;
    TextView resend;
    TextView worry;
    TextView refill;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.third_frag, container, false);

        Typeface mTypeface = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);
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

    public static ThirdTutorialFragment newInstance(String text) {

        ThirdTutorialFragment fragmentThird = new ThirdTutorialFragment();
        Bundle args = new Bundle();

        fragmentThird.setArguments(args);

        return fragmentThird;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
}
