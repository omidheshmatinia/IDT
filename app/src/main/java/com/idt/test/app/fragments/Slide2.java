package com.idt.test.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idt.test.app.R;

/**
 * Created by Omid Heshmatinia on 9/2/2016.
 */
    public class Slide2 extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.intro2, container, false);
            return v;
        }
    }

