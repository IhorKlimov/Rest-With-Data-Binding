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

public class TypeAdapter implements JsonDeserializer<List<Movie>> {
    private static final String LOG_TAG = "TypeAdapter";

    @Override
    public List<Movie> deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {


        return extractMovies(json);
    }

    @NonNull
    private ArrayList<Movie> extractMovies(JsonElement json) {
        JsonArray arr = json.getAsJsonObject()
                .get("results").getAsJsonArray();


        ArrayList<Movie> list = new ArrayList<>();

        for (JsonElement element : arr) {
            extractMovie(list, element);
        }
        return list;
    }

    private void extractMovie(ArrayList<Movie> list, JsonElement element) {
        JsonObject j = element.getAsJsonObject();

        String title = j.get("original_title").getAsString();
        String posterUrl = "http://image.tmdb.org/t/p/w300/" + j.get("poster_path").getAsString();
        String backdropUrl = "http://image.tmdb.org/t/p/w500/" + j.get("backdrop_path").getAsString();
        float vote = j.get("vote_average").getAsFloat();
        String id = j.get("id").getAsString();
        String description = j.get("overview").getAsString();

        list.add(new Movie(posterUrl, title, backdropUrl, vote, description, id));
    }
}
