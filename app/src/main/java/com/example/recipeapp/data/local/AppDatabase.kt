package com.example.recipeapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipeapp.data.models.Meal
import com.example.recipeapp.data.models.User

@Database(entities = [Meal::class, User::class ], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_app_db"
                )
                    .fallbackToDestructiveMigration() // Delete and re-create the database
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}