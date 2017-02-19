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

import android.util.Log
import com.google.gson.*
import com.myhexaville.restwithdatabinding.movies.Movie
import java.lang.reflect.Type
import java.util.*

class TypeAdapter : JsonDeserializer<List<Movie>> {

    @Throws(JsonParseException::class)
    override fun deserialize(
            json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<Movie> {
        Log.d("list", "hey")


        return extractMovies(json)
    }

    private fun extractMovies(json: JsonElement): ArrayList<Movie> {
        val arr = json.asJsonObject
                .get("results").asJsonArray
        Log.d("list", json.toString())

        val list = ArrayList<Movie>()

        for (element in arr) {
            extractMovie(list, element)
        }
        return list
    }

    private fun extractMovie(list: ArrayList<Movie>, element: JsonElement) {
        val j = element.asJsonObject




        val title = j.get("original_title").asString
        val poster = j.get("poster_path")
        if (poster is JsonNull) {
            return
        }
        val posterUrl = "http://image.tmdb.org/t/p/w300/" + poster.asString
        val backdrop = j.get("backdrop_path")
        if (backdrop is JsonNull) {
            return
        }
        val backdropUrl = "http://image.tmdb.org/t/p/w500/" + backdrop.asString
        val vote = j.get("vote_average").asFloat
        val id = j.get("id").asString
        val description = j.get("overview").asString


        list.add(Movie(posterUrl, title, backdropUrl, vote, description, id, null))
    }

    companion object {
        private val LOG_TAG = "TypeAdapter"
    }
}
