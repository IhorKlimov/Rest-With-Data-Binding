package com.myhexaville.restwithdatabinding;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.myhexaville.restwithdatabinding.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

//        setupToolbar();

        setupPager();
    }

//    private void setupToolbar() {
//        setSupportActionBar(mBinding.toolbar);
//    }

    private void setupPager() {
        mBinding.pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        mBinding.tabs.setupWithViewPager(mBinding.pager);
    }
}
