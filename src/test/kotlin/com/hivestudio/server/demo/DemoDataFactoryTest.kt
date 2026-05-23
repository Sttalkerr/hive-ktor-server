package com.hivestudio.server.demo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DemoDataFactoryTest {
    @Test
    fun demoFactoryReturnsTwoSeedBeats() {
        val beats = DemoDataFactory.beats()
        assertEquals(2, beats.size)
        assertTrue(beats.any { it.title == "Midnight Pulse" })
        assertTrue(beats.any { it.title == "Velvet Echo" })
    }
}
