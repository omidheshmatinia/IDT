package com.idt.test.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

import com.github.paolorotolo.appintro.AppIntro;
import com.idt.test.app.R;
import com.idt.test.app.fragments.Slide1;
import com.idt.test.app.fragments.Slide2;
import com.idt.test.app.fragments.Slide3;
import com.idt.test.app.function.MyPreference;

/**
 * Created by Omid Heshmatinia on 9/2/2016.
 */
public class IntroActivity extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {

        if(MyPreference.getInstance(this).isFirstVisit()) {
            addSlide(new Slide1());
            addSlide(new Slide2());
            addSlide(new Slide3());

            setBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            showSkipButton(false);
            setProgressButtonEnabled(true);

            ((Button) doneButton).setText("Skip");
            pager.setBackgroundColor(ContextCompat.getColor(this, R.color.md_grey_300));
        } else {
            onDonePressed();
        }
    }

    @Override
    public void onSkipPressed() {

    }

    @Override
    public void onDonePressed() {
        MyPreference.getInstance(this).setFirstVisit(false);
        Intent mIntent=new Intent(this,MainActivity.class);
        startActivity(mIntent);
        finish();
    }

    @Override
    public void onNextPressed() {}

    @Override
    public void onSlideChanged() {}


}
