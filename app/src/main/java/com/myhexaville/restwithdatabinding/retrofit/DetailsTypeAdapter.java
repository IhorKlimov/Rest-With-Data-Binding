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

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.myhexaville.restwithdatabinding.movies.Movie;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ihor on 2017-01-22.
 */
public class DetailsTypeAdapter implements JsonDeserializer<Movie> {
    private static final String LOG_TAG = "DetailsTypeAdapter";

    @Override
    public Movie deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return extractMovie(json);
    }

    @NonNull
    private Movie extractMovie(JsonElement json) {
        JsonObject j = json.getAsJsonObject();

        String title = j.get("original_title").getAsString();
        String posterUrl = "http://image.tmdb.org/t/p/w300/" + j.get("poster_path").getAsString();
        String backdropUrl = "http://image.tmdb.org/t/p/w500/" + j.get("backdrop_path").getAsString();
        float vote = j.get("vote_average").getAsFloat();
        String id = j.get("id").getAsString();
        String description = j.get("overview").getAsString();

        List<String> productionCompanies = null;
        if (j.has("production_companies")) {
            productionCompanies = extractProductionCompanies(j);
        }

        return new Movie(posterUrl, title, backdropUrl, vote, description, id, productionCompanies);
    }

    @NonNull
    private List<String> extractProductionCompanies(JsonElement json) {
        JsonArray arr = json.getAsJsonObject()
                .getAsJsonArray("production_companies");

        ArrayList<String> list = new ArrayList<>();

        for (JsonElement element : arr) {
            JsonObject j = element.getAsJsonObject();

            String productionCompany = j.get("name").getAsString();

            list.add(productionCompany);
        }

        return list;
    }

}

