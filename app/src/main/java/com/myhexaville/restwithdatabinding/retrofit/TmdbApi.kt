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

package com.myhexaville.restwithdatabinding.retrofit

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.myhexaville.restwithdatabinding.movies.Movie
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmdbApi {
    val BASE_URL = "http://api.themoviedb.org/3/"

    var instance: TmdbService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverter)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(TmdbService::class.java)

    val gsonConverter: GsonConverterFactory
        get() {
            val gson = GsonBuilder()
                    .registerTypeAdapter(List::class.java, TypeAdapter())
                    .registerTypeAdapter(Movie::class.java, DetailsTypeAdapter())
                    .create()
            return GsonConverterFactory.create(gson)
        }

}
