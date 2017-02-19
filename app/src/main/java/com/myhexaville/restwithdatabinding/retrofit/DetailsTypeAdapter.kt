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

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.myhexaville.restwithdatabinding.movies.Movie
import java.lang.reflect.Type

/**
 * Created by ihor on 2017-01-22.
 */
class DetailsTypeAdapter : JsonDeserializer<Movie> {

    @Throws(JsonParseException::class)
    override fun deserialize(
            json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Movie {
        return extractMovie(json)
    }

    private fun extractMovie(json: JsonElement): Movie {
        val j = json.asJsonObject

        val title = j.get("original_title").asString
        val posterUrl = "http://image.tmdb.org/t/p/w300/" + j.get("poster_path").asString
        val backdropUrl = "http://image.tmdb.org/t/p/w500/" + j.get("backdrop_path").asString
        val vote = j.get("vote_average").asFloat
        val id = j.get("id").asString
        val description = j.get("overview").asString

        var productionCompanies: List<String>? = null
        if (j.has("production_companies")) {
            productionCompanies = extractProductionCompanies(j)
        }

        return Movie(posterUrl, title, backdropUrl, vote, description, id, productionCompanies)
    }

    private fun extractProductionCompanies(json: JsonElement): List<String> {
        val arr = json.asJsonObject.getAsJsonArray("production_companies")

        val list = arr
                .map { it.asJsonObject.get("name").asString }

        return list
    }

    companion object {
        private val LOG_TAG = "DetailsTypeAdapter"
    }
}


