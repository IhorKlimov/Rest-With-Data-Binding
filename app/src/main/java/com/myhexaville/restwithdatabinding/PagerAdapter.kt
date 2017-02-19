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

package com.myhexaville.restwithdatabinding

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

import com.myhexaville.restwithdatabinding.firebase.RxJavaFirebaseFragment
import com.myhexaville.restwithdatabinding.movies.MoviesListFragment

class PagerAdapter(fm: FragmentManager, var sortType: String) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            return MoviesListFragment.newInstance(sortType)
        } else {
            return RxJavaFirebaseFragment()
        }
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence {
        if (position == 0) {
            return "Movies"
        } else {
            return "Firebase"
        }
    }
}
