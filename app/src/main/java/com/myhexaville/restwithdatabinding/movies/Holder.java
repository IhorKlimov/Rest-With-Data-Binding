/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.myhexaville.restwithdatabinding.movies;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.myhexaville.restwithdatabinding.databinding.ListItemBinding;
import com.myhexaville.restwithdatabinding.movies.DetailsActivity;


public class Holder extends RecyclerView.ViewHolder {
    private Activity mActivity;
    ListItemBinding mBinding;
    Movie mMovie;

    public Holder(Activity a, View itemView) {
        super(itemView);
        mActivity = a;

        mBinding = DataBindingUtil.bind(itemView);
        final Context context = itemView.getContext();

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailsActivity.class)
                    .putExtra(DetailsActivity.Companion.getMOVIE(), mMovie);

            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(mActivity, mBinding.poster, "poster")
                    .toBundle();
            context.startActivity(intent, bundle);
        });
    }

    public void setMovie(Movie movie) {
        mMovie = movie;
    }
}
