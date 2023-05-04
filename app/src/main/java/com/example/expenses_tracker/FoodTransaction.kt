package com.example.expenses_tracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FoodTransactions")
data class FoodTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val label:String,
    val amount:Double,
    val description: String) : java.io.Serializable {
}