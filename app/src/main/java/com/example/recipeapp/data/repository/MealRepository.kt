package com.example.recipeapp.data.repository


import com.example.recipeapp.data.local.AppDatabase
import com.example.recipeapp.data.models.Meal
import com.example.recipeapp.data.models.User
import com.example.recipeapp.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow

class MealRepository(private val db: AppDatabase) {

    // Meal Functions
    suspend fun searchMeals(searchQuery: String) = RetrofitInstance.api.searchMeals(searchQuery)
    suspend fun upsertMeal(meal: Meal) = db.mealDao().upsertMeal(meal)
    suspend fun deleteMeal(meal: Meal) = db.mealDao().deleteMeal(meal)
    fun getFavoriteMeals(): Flow<List<Meal>> = db.mealDao().getAllFavoriteMeals()

    suspend fun registerUser(user: User) = db.userDao().registerUser(user)
    suspend fun getUserByEmail(email: String): User? = db.userDao().getUserByEmail(email)

}