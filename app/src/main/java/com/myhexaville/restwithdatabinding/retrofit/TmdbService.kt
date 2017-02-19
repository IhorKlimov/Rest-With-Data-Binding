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

import com.myhexaville.restwithdatabinding.movies.Movie
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbService {
    @GET("discover/movie")
    fun findMovies(
            @Query("sort_by") sortBy: String,
            @Query("page") page: String,
            @Query("api_key") apiKey: String): Observable<List<Movie>>

    @GET("movie/{id}")
    fun getDetails(
            @Path("id") id: String,
            @Query("api_key") apiKey: String): Observable<Movie>

}
