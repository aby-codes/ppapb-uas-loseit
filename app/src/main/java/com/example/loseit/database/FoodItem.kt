package com.example.loseit.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val time: String,
    val name: String,
    val calories: Int,
    val serving: Int,
    val userEmail: String,
)
