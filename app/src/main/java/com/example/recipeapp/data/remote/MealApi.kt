package com.example.recipeapp.data.remote

import MealsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("api/json/v1/1/search.php")
    suspend fun searchMeals(@Query("s") searchQuery: String): MealsResponse


}