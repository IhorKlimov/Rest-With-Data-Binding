package com.myhexaville.restwithdatabinding.movies;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.myhexaville.restwithdatabinding.R;
import com.myhexaville.restwithdatabinding.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {
    private static final String LOG_TAG = "DetailsActivity";
    public static final String MOVIE = "Movie";
    private ActivityDetailsBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        Movie m = getIntent().getParcelableExtra(MOVIE);

        mBinding.setMovie(m);

        setPosterPosition();
    }

    private void setPosterPosition() {
        int bh = getBackdropHeight();

        int ph = getPosterHeight();


        Log.d(LOG_TAG, "setPosterPosition: " + bh);

        PercentRelativeLayout.LayoutParams layoutParams =
                (PercentRelativeLayout.LayoutParams) mBinding.poster.getLayoutParams();
        layoutParams.topMargin = bh - ph / 2;
    }

    public int getBackdropHeight() {
        return (int) (getResources().getDisplayMetrics().widthPixels * .5625f);
    }

    public int getPosterHeight() {
        return (int) (getResources().getDisplayMetrics().widthPixels * .30f * 1.5384f);
    }
}
