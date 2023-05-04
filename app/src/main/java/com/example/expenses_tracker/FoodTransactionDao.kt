package com.example.expenses_tracker

import androidx.room.*

@Dao
interface FoodTransactionDao {

    @Query("SELECT * from foodtransactions")
    fun getAll(): List<FoodTransaction>

    @Insert
    fun insetAll(vararg transaction: FoodTransaction)

    @Delete
    fun delete(transaction:FoodTransaction)

    @Update
    fun update(vararg transaction: FoodTransaction)
}