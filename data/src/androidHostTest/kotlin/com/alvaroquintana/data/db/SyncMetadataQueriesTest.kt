package com.alvaroquintana.data.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SyncMetadataQueriesTest {

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: AdivinaRazaDatabase
    private lateinit var queries: SyncMetadataQueries

    @Before
    fun setup() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AdivinaRazaDatabase.Schema.create(driver)
        database = AdivinaRazaDatabase(driver)
        queries = database.syncMetadataQueries
    }

    @After
    fun tearDown() {
        driver.close()
    }

    @Test
    fun upsert_and_getByCollection_round_trip() {
        queries.upsert(collection = "breeds_es", lastSyncTimestamp = 1700000000000)

        val result = queries.getByCollection("breeds_es").executeAsOneOrNull()

        assertNotNull(result)
        assertEquals("breeds_es", result!!.collection)
        assertEquals(1700000000000, result.lastSyncTimestamp)
    }

    @Test
    fun getByCollection_returns_null_for_unknown_collection() {
        val result = queries.getByCollection("nonexistent").executeAsOneOrNull()

        assertNull(result)
    }

    @Test
    fun upsert_overwrites_existing_entry() {
        queries.upsert(collection = "breeds_es", lastSyncTimestamp = 1000)
        queries.upsert(collection = "breeds_es", lastSyncTimestamp = 2000)

        val result = queries.getByCollection("breeds_es").executeAsOneOrNull()

        assertNotNull(result)
        assertEquals(2000, result!!.lastSyncTimestamp)
    }

    @Test
    fun upsert_supports_multiple_collections() {
        queries.upsert(collection = "breeds_es", lastSyncTimestamp = 1000)
        queries.upsert(collection = "breeds_en", lastSyncTimestamp = 2000)
        queries.upsert(collection = "apps", lastSyncTimestamp = 3000)

        val es = queries.getByCollection("breeds_es").executeAsOneOrNull()
        val en = queries.getByCollection("breeds_en").executeAsOneOrNull()
        val apps = queries.getByCollection("apps").executeAsOneOrNull()

        assertEquals(1000, es!!.lastSyncTimestamp)
        assertEquals(2000, en!!.lastSyncTimestamp)
        assertEquals(3000, apps!!.lastSyncTimestamp)
    }

    @Test
    fun upsert_preserves_other_collections_on_update() {
        queries.upsert(collection = "breeds_es", lastSyncTimestamp = 1000)
        queries.upsert(collection = "breeds_en", lastSyncTimestamp = 2000)

        queries.upsert(collection = "breeds_es", lastSyncTimestamp = 9999)

        val es = queries.getByCollection("breeds_es").executeAsOneOrNull()
        val en = queries.getByCollection("breeds_en").executeAsOneOrNull()

        assertEquals(9999, es!!.lastSyncTimestamp)
        assertEquals(2000, en!!.lastSyncTimestamp)
    }
}
