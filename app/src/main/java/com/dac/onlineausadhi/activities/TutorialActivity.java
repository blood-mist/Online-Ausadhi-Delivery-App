package com.dac.onlineausadhi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.dac.onlineausadhi.fragments.Slider;
import com.dac.onlineausadhi.onlineaushadhilin.R;
import com.github.paolorotolo.appintro.AppIntro;

public class TutorialActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(Slider.newInstance(R.layout.first_frag));
        addSlide(Slider.newInstance(R.layout.second_frag));
        addSlide(Slider.newInstance(R.layout.third_frag));
        setIndicatorColor(Color.BLUE,Color.RED);
        setColorSkipButton(Color.RED);
        setNextArrowColor(Color.RED);
        setColorDoneText(Color.RED);
        setVibrate(true);
        setVibrateIntensity(30);

    }
    private void loadMainActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
      loadMainActivity();
    }
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent =new Intent(TutorialActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
    }

