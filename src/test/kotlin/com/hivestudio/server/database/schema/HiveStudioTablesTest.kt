package com.hivestudio.server.database.schema

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HiveStudioTablesTest {
    @Test
    fun allSchemaTablesAreRegistered() {
        assertEquals(4, HiveStudioTables.allTables.size)
        assertTrue(HiveStudioTables.allTables.any { it.tableName == "producers" })
        assertTrue(HiveStudioTables.allTables.any { it.tableName == "beats" })
        assertTrue(HiveStudioTables.allTables.any { it.tableName == "beat_statistics" })
        assertTrue(HiveStudioTables.allTables.any { it.tableName == "beat_events" })
    }
}
