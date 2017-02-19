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

package com.myhexaville.restwithdatabinding.firebase

import android.util.Pair

import com.google.firebase.auth.AuthResult
import com.google.firebase.storage.UploadTask

import io.reactivex.Flowable

class Registration {
    var user: Flowable<AuthResult>? = null
    var uploadPicture: Flowable<Pair<UploadTask.TaskSnapshot, String>>? = null
}
