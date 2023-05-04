package com.example.expenses_tracker

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(FoodTransaction::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodTransactionDao():FoodTransactionDao
}