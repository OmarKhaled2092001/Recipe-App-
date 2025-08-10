package com.example.recipeapp.data.remote

import MealsResponse
import com.example.recipeapp.data.models.CategoriesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("api/json/v1/1/search.php")
    suspend fun searchMeals(@Query("s") searchQuery: String): MealsResponse

    @GET("api/json/v1/1/categories.php")
    suspend fun getCategories(): CategoriesResponse

    @GET("api/json/v1/1/filter.php")
    suspend fun getMealsByCategory(@Query("c") categoryName: String): MealsResponse

}