package com.myhexaville.restwithdatabinding.movies

import android.app.Activity
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.target.ImageViewTarget
import com.myhexaville.restwithdatabinding.R
import com.myhexaville.restwithdatabinding.databinding.ListItemBinding

import com.myhexaville.restwithdatabinding.Utils.sizeOf


class Adapter(private val mActivity: Activity) : RecyclerView.Adapter<Holder>() {
    private var mMovies: List<Movie>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = DataBindingUtil
                .inflate<ListItemBinding>(LayoutInflater.from(parent.context), R.layout.list_item, parent, false)

        return Holder(mActivity, binding.root)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val m = mMovies!![position]
        holder.mBinding.movie = m
        holder.setMovie(m)

        Glide.with(mActivity)
                .load(m.posterUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(object : ImageViewTarget<GlideDrawable>(holder.mBinding.poster) {
                    override fun setResource(resource: GlideDrawable) {
                        setImage(resource)

                        if (!hasExtractedColorAlready()) {
                            extractColor(resource)
                        }
                    }

                    private fun hasExtractedColorAlready(): Boolean {
                        return m.color != 0
                    }

                    private fun setImage(resource: GlideDrawable) {
                        holder.mBinding.poster.setImageDrawable(resource.current)
                    }

                    private fun extractColor(resource: GlideDrawable) {
                        val b = (resource.current as GlideBitmapDrawable).bitmap
                        val p = Palette.from(b).generate()
                        val defaultColor = mActivity.resources.getColor(R.color.colorPrimary)
                        val color = p.getDarkMutedColor(defaultColor)

                        m.color = color
                    }
                })
    }

    override fun getItemCount(): Int {
        return sizeOf(mMovies)
    }

    fun setmMovies(movies: List<Movie>) {
        mMovies = movies
        notifyDataSetChanged()
    }

    companion object {
        private val LOG_TAG = "Adapter"
    }

}
