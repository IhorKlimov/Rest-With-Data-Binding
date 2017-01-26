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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;

public class Movie implements Parcelable {
    private String mPosterUrl, mTitle, mBackdropUrl, mDescription, mId;
    private float mVotes;
    private int mColor;
    private List<String> mProductionCompanies;


    public Movie(String posterUrl, String title, String backgroupUrl, float votes,
                 String description, String id, List<String> productionCompanies) {
        mPosterUrl = posterUrl;
        mTitle = title;
        mBackdropUrl = backgroupUrl;
        mVotes = votes;
        mDescription = description;
        mId = id;
        mProductionCompanies = productionCompanies;
    }


    protected Movie(Parcel in) {
        mPosterUrl = in.readString();
        mTitle = in.readString();
        mBackdropUrl = in.readString();
        mDescription = in.readString();
        mVotes = in.readFloat();
        mColor = in.readInt();
        mId = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public String getBackdropUrl() {
        return mBackdropUrl;
    }

    public float getVote() {
        return mVotes;
    }

    @NonNull
    public String getPosterUrl() {
        return mPosterUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mPosterUrl);
        parcel.writeString(mTitle);
        parcel.writeString(mBackdropUrl);
        parcel.writeString(mDescription);
        parcel.writeFloat(mVotes);
        parcel.writeInt(mColor);
        parcel.writeString(mId);
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public List<String> getProductionCompanies() {
        return mProductionCompanies;
    }

    public void setProductionCompanies(List<String> productionCompanies) {
        mProductionCompanies = productionCompanies;
    }
}
