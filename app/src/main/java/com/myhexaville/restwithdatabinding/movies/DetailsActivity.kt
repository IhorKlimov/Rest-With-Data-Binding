package com.myhexaville.restwithdatabinding.movies

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.databinding.DataBindingUtil
import android.graphics.drawable.Icon
import android.os.Bundle
import android.support.percent.PercentRelativeLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.target.ImageViewTarget
import com.myhexaville.restwithdatabinding.Constants.API_KEY
import com.myhexaville.restwithdatabinding.R
import com.myhexaville.restwithdatabinding.databinding.ActivityDetailsBinding
import com.myhexaville.restwithdatabinding.retrofit.TmdbApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*


class DetailsActivity : AppCompatActivity() {
    private var binding: ActivityDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityDetailsBinding>(this, R.layout.activity_details)

        setupToolbar()

        setupMovie()

        setPosterPosition()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding!!.toolbar)
        val bar = supportActionBar
        bar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share && binding!!.movie != null) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "https://damp-sea-27839.herokuapp.com/movie/" + binding!!.movie.id)
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
            binding!!.movie = m
        } else {
            val movieId = intent.extras?.getString("movie id") ?: intent.data.lastPathSegment
            TmdbApi.instance.getDetails(movieId, API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : DisposableObserver<Movie>() {
                        override fun onNext(value: Movie) {
                            Log.d(LOG_TAG, "onNext: ")
                            Glide.with(baseContext)
                                    .load(value.posterUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .into(object : ImageViewTarget<GlideDrawable>(binding!!.poster) {
                                        override fun setResource(resource: GlideDrawable) {
                                            setImage(resource)

                                            extractColor(resource)
                                        }

                                        private fun setImage(resource: GlideDrawable) {
                                            binding!!.poster.setImageDrawable(resource.current)
                                        }

                                        private fun extractColor(resource: GlideDrawable) {
                                            val b = (resource.current as GlideBitmapDrawable).bitmap
                                            val p = Palette.from(b).generate()
                                            val defaultColor = ContextCompat.getColor(baseContext, R.color.colorPrimary)
                                            val color = p.getDarkMutedColor(defaultColor)

                                            value.color = color
                                            binding!!.movie = value
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

        val layoutParams = binding!!.poster.layoutParams as PercentRelativeLayout.LayoutParams
        layoutParams.topMargin = bh - ph / 2
    }

    fun addToShortcut(v: View) {
        val shortcutManager = getSystemService(ShortcutManager::class.java)

        val intent = Intent(Intent.ACTION_MAIN, null, this, DetailsActivity::class.java)
                .putExtra("movie id", binding!!.movie.id)

        val shortcut = ShortcutInfo.Builder(this, "id1")
                .setShortLabel(binding!!.movie.title)
                .setLongLabel(binding!!.movie.title)
                .setIcon(Icon.createWithResource(this, R.drawable.movie))
                .setIntent(intent)
                .build()

        shortcutManager.dynamicShortcuts = Arrays.asList(shortcut)

        Toast.makeText(this, "Created a shortcut", LENGTH_SHORT).show()
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
