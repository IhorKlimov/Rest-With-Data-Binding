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

package com.myhexaville.restwithdatabinding.firebase;

import android.util.Pair;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.UploadTask;

import io.reactivex.Flowable;

public class Registration {
    private Flowable<AuthResult> mUser;
    private Flowable<Pair<UploadTask.TaskSnapshot, String>> mUploadPicture;

    public void setUser(Flowable<AuthResult> user) {
        mUser = user;
    }

    public void setUpload(Flowable<Pair<UploadTask.TaskSnapshot, String>> uploadPicture) {
        mUploadPicture = uploadPicture;
    }

    public Flowable<AuthResult> getUser() {
        return mUser;
    }

    public Flowable<Pair<UploadTask.TaskSnapshot, String>> getUploadPicture() {
        return mUploadPicture;
    }
}
