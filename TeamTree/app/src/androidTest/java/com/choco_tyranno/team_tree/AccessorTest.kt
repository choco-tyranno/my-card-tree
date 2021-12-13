package com.choco_tyranno.team_tree

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
class AccessorTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun test() {
        val testText = "changed"
        val item = TestItem()
//        Log.d(TAG,"lateinit var? : ${item.getNonInitializedA1()}")
//        -> makes Uninitialized error.
        Log.d(TAG, "[Not initialized case]a1:${item.getA1()}")
        item.setA1(testText)
        Log.d(TAG, "[Initialized case]a1:${item.getA1()}")
        Assert.assertEquals(testText, item.getA1())
    }

    companion object {
        const val TAG = "@@AccessorTest"
    }
}

class TestItem {
    private lateinit var a1: String
    lateinit var a2: String

    fun setA1(str: String) {
        a1 = str
    }

    fun getA1(): String {
        if (::a1.isInitialized)
            return a1
        else
            return "not initialized"
    }

    fun getNonInitializedA1(): String{
        return a1
    }

}