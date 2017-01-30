package com.myhexaville.restwithdatabinding

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.myhexaville.restwithdatabinding.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)


        setupPager()
    }

    private fun setupPager() {
        mBinding!!.pager.adapter = PagerAdapter(supportFragmentManager)
        mBinding!!.tabs.setupWithViewPager(mBinding!!.pager)
    }
}
