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

package com.myhexaville.restwithdatabinding.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.myhexaville.restwithdatabinding.movies.Movie;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TmdbApi {
    public static final String BASE_URL = "http://api.themoviedb.org/3/";

    private static TmdbService sInstance;


    public static TmdbService getInstance() {
        if (sInstance == null) {
            sInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(getGsonConverter())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(TmdbService.class);
        }

        return sInstance;
    }

    private static GsonConverterFactory getGsonConverter() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Movie>>() {}.getType(), new TypeAdapter())
                .registerTypeAdapter(new TypeToken<List<String>>() {}.getType(), new DetailsTypeAdapter())
                .create();
        return GsonConverterFactory.create(gson);
    }

}
