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

package com.myhexaville.restwithdatabinding.movies

import android.os.Parcel
import android.os.Parcelable

class Movie : Parcelable {
    var posterUrl: String? = null
    var title: String? = null
    var backdropUrl: String? = null
    var description: String? = null
    var id: String? = null
    var vote: Float = 0.toFloat()
    var color: Int = 0
    var productionCompanies: List<String>? = null

    constructor(posterUrl: String, title: String, backgroupUrl: String, votes: Float,
                description: String, id: String, productionCompanies: List<String>?) {
        this.posterUrl = posterUrl
        this.title = title
        backdropUrl = backgroupUrl
        vote = votes
        this.description = description
        this.id = id
        this.productionCompanies = productionCompanies
    }

    protected constructor(`in`: Parcel) {
        posterUrl = `in`.readString()
        title = `in`.readString()
        backdropUrl = `in`.readString()
        description = `in`.readString()
        vote = `in`.readFloat()
        color = `in`.readInt()
        id = `in`.readString()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(posterUrl)
        parcel.writeString(title)
        parcel.writeString(backdropUrl)
        parcel.writeString(description)
        parcel.writeFloat(vote)
        parcel.writeInt(color)
        parcel.writeString(id)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Movie> = object : Parcelable.Creator<Movie> {
            override fun createFromParcel(`in`: Parcel): Movie {
                return Movie(`in`)
            }

            override fun newArray(size: Int): Array<Movie?> {
                return arrayOfNulls(size)
            }
        }
    }
}
