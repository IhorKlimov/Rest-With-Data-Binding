package com.myhexaville.restwithdatabinding.movies;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.myhexaville.restwithdatabinding.R;
import com.myhexaville.restwithdatabinding.databinding.ListItemBinding;

import java.util.List;

import static com.myhexaville.restwithdatabinding.Utils.sizeOf;


public class Adapter extends RecyclerView.Adapter<Holder> {
    private static final String LOG_TAG = "Adapter";
    private List<Movie> mMovies;
    private Activity mActivity;

    public Adapter(Activity c) {
        mActivity = c;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item, parent, false);

        return new Holder(mActivity, binding.getRoot());
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final Movie m = mMovies.get(position);
        holder.mBinding.setMovie(m);
        holder.setMovie(m);

        Glide.with(mActivity)
                .load(m.getPosterUrl())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new ImageViewTarget<GlideDrawable>(holder.mBinding.poster) {
                    @Override
                    protected void setResource(GlideDrawable resource) {
                        setImage(resource);

                        if (!hasExtractedColorAlready()) {
                            extractColor(resource);
                        }
                    }

                    private boolean hasExtractedColorAlready() {
                        return m.getColor() != 0;
                    }

                    private void setImage(GlideDrawable resource) {
                        holder.mBinding.poster.setImageDrawable(resource.getCurrent());
                    }

                    private void extractColor(GlideDrawable resource) {
                        Bitmap b = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                        Palette p = Palette.from(b).generate();
                        int defaultColor = mActivity.getResources().getColor(R.color.colorPrimary);
                        int color = p.getDarkMutedColor(defaultColor);

                        m.setColor(color);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return sizeOf(mMovies);
    }

    public void setmMovies(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

}
