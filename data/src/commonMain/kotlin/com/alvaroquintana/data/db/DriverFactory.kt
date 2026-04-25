package com.alvaroquintana.data.db

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): AdivinaRazaDatabase {
    return AdivinaRazaDatabase(driverFactory.createDriver())
}
