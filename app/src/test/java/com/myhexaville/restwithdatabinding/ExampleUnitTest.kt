package com.myhexaville.restwithdatabinding

import com.myhexaville.restwithdatabinding.movies.Movie
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        println(15 % 4)
        var i: Movie? = null
        println(i?.backdropUrl ?: "no")
        assertEquals(4, (2 + 2).toLong())
    }
}