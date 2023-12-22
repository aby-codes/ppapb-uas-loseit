package com.example.loseit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FoodItem::class], version = 1)
abstract class FoodRoomDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao

    companion object {
        @Volatile
        private var instance: FoodRoomDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context.applicationContext).also {
                instance = it
            }
        }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                FoodRoomDatabase::class.java,
                "foodItem_database"
            ).build()
    }
}