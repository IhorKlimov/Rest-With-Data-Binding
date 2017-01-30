package com.myhexaville.restwithdatabinding.movies

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Bundle
import android.support.percent.PercentRelativeLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.target.ImageViewTarget
import com.myhexaville.restwithdatabinding.R
import com.myhexaville.restwithdatabinding.databinding.ActivityDetailsBinding
import com.myhexaville.restwithdatabinding.retrofit.TmdbApi

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

import com.myhexaville.restwithdatabinding.Constants.API_KEY

class DetailsActivity : AppCompatActivity() {
    private var mBinding: ActivityDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView<ActivityDetailsBinding>(this, R.layout.activity_details)

        setupToolbar()

        setupMovie()

        setPosterPosition()
    }

    private fun setupToolbar() {
        setSupportActionBar(mBinding!!.toolbar)
        val bar = supportActionBar
        bar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share && mBinding!!.movie != null) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "https://damp-sea-27839.herokuapp.com/movie/" + mBinding!!.movie.id)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)

            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    private fun setupMovie() {
        val m = intent.getParcelableExtra<Movie>(MOVIE)

        if (m != null) {
            mBinding!!.movie = m
        } else {
            val lastPathSegment = intent.data.lastPathSegment ?: return
            Log.d(LOG_TAG, "setupMovie: " + lastPathSegment)
            TmdbApi.getInstance().getDetails(lastPathSegment, API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : DisposableObserver<Movie>() {
                        override fun onNext(value: Movie) {
                            Log.d(LOG_TAG, "onNext: ")
                            mBinding!!.movie = value
                            Glide.with(baseContext)
                                    .load(value.posterUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .into(object : ImageViewTarget<GlideDrawable>(mBinding!!.poster) {
                                        override fun setResource(resource: GlideDrawable) {
                                            setImage(resource)

                                            extractColor(resource)
                                        }

                                        private fun setImage(resource: GlideDrawable) {
                                            mBinding!!.poster.setImageDrawable(resource.current)
                                        }

                                        private fun extractColor(resource: GlideDrawable) {
                                            val b = (resource.current as GlideBitmapDrawable).bitmap
                                            val p = Palette.from(b).generate()
                                            val defaultColor = ContextCompat.getColor(baseContext, R.color.colorPrimary)
                                            val color = p.getDarkMutedColor(defaultColor)

                                            mBinding!!.root.setBackgroundColor(color)
                                        }
                                    })
                        }

                        override fun onError(e: Throwable) {

                        }

                        override fun onComplete() {

                        }
                    })
        }
    }

    private fun setPosterPosition() {
        val bh = backdropHeight

        val ph = posterHeight


        Log.d(LOG_TAG, "setPosterPosition: " + bh)

        val layoutParams = mBinding!!.poster.layoutParams as PercentRelativeLayout.LayoutParams
        layoutParams.topMargin = bh - ph / 2
    }

    val backdropHeight: Int
        get() = (resources.displayMetrics.widthPixels * .5625f).toInt()

    val posterHeight: Int
        get() = (resources.displayMetrics.widthPixels.toFloat() * .30f * 1.5384f).toInt()

    companion object {
        private val LOG_TAG = "DetailsActivity"
        val MOVIE = "Movie"
    }
}
