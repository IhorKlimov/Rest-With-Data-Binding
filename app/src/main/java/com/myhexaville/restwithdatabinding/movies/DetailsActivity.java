package com.myhexaville.restwithdatabinding.movies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.myhexaville.restwithdatabinding.R;
import com.myhexaville.restwithdatabinding.databinding.ActivityDetailsBinding;
import com.myhexaville.restwithdatabinding.retrofit.TmdbApi;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.myhexaville.restwithdatabinding.Constants.API_KEY;

public class DetailsActivity extends AppCompatActivity {
    private static final String LOG_TAG = "DetailsActivity";
    public static final String MOVIE = "Movie";
    private ActivityDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        setupToolbar();

        setupMovie();

        setPosterPosition();
    }

    private void setupToolbar() {
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share && mBinding.getMovie() != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "https://damp-sea-27839.herokuapp.com/movie/" + mBinding.getMovie().getId());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setupMovie() {
        Movie m = getIntent().getParcelableExtra(MOVIE);

        if (m != null) {
            mBinding.setMovie(m);
            loadPoster(m);
        } else {
            String lastPathSegment = getIntent().getData().getLastPathSegment();
            if (lastPathSegment == null) {
                return;
            }
            Log.d(LOG_TAG, "setupMovie: " + lastPathSegment);
            TmdbApi.getInstance().getDetails(lastPathSegment, API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<Movie>() {
                        @Override
                        public void onNext(Movie value) {
                            Log.d(LOG_TAG, "onNext: ");
                            mBinding.setMovie(value);
                            Glide.with(getBaseContext())
                                    .load(value.getPosterUrl())
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .into(new ImageViewTarget<GlideDrawable>(mBinding.poster) {
                                        @Override
                                        protected void setResource(GlideDrawable resource) {
                                            setImage(resource);

                                            extractColor(resource);
                                        }

                                        private void setImage(GlideDrawable resource) {
                                            mBinding.poster.setImageDrawable(resource.getCurrent());
                                        }

                                        private void extractColor(GlideDrawable resource) {
                                            Bitmap b = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                                            Palette p = Palette.from(b).generate();
                                            int defaultColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimary);
                                            int color = p.getDarkMutedColor(defaultColor);

                                            mBinding.root.setBackgroundColor(color);
                                        }
                                    });
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    private void loadPoster(Movie m) {
        Glide.with(this)
                .load(m.getPosterUrl())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mBinding.poster);
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
