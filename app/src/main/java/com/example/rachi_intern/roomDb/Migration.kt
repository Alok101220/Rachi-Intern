package com.example.rachi_intern.roomDb

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL("ALTER TABLE Address ADD COLUMN userName Text")

    }
}
