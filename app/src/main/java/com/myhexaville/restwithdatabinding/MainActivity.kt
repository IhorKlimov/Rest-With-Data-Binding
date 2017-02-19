package com.myhexaville.restwithdatabinding

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.myhexaville.restwithdatabinding.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val LOG_TAG = "MainActivity"

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        setupPager()
    }

    private fun setupPager() {
        val sortType = getSortType()

        binding!!.pager.adapter = PagerAdapter(supportFragmentManager, sortType)
        binding!!.tabs.setupWithViewPager(binding!!.pager)
    }

    private fun getSortType(): String {
        return if (intent.extras != null && intent.extras.containsKey("sort")) {
            intent.extras.getString("sort")
        } else {
            "popular"
        }
    }
}
