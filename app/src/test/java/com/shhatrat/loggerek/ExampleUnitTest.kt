package com.shhatrat.loggerek

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        assertEquals(4, (2 + 2).toLong())
    }

    @Test
    @Throws(Exception::class)
    fun getOP() {
        val i = "http://opencaching.pl/viewcache.php?wp=OP82DU"
        val o = i.lastIndexOf("OP")
        println(i.substring(o, i.length))
        assertEquals(4, (2 + 2).toLong())
    }
}