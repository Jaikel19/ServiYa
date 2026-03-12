package com.example.shared.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DriverFactory {

    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            AppDatabase.Schema,
            "serviya.db"
        )
    }
}