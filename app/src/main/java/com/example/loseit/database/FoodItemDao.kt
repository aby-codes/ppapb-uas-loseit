package com.example.loseit.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface FoodItemDao {
    @Insert
     fun insert(foodItem: FoodItem)

    @Delete
     fun deleteFood(foodItem: FoodItem)

    @Query("SELECT * FROM food_items")
     fun getAllFoodItems(): List<FoodItem>

    @Query("SELECT * FROM food_items WHERE id=:food_id")
     fun getFood(food_id: Int):List<FoodItem>

    @Query("SELECT * FROM food_items WHERE userEmail=:food_userEmail")
     fun getFoodByUser(food_userEmail: String):List<FoodItem>

    @Query("SELECT SUM(calories) FROM food_items WHERE userEmail = :userEmail")
    fun getTotalCaloriesForUser(userEmail: String): Int
}