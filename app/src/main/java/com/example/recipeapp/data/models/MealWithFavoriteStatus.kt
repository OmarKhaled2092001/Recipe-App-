package com.example.recipeapp.data.models

data class MealWithFavoriteStatus(
    val meal: Meal,
    var isFavorite: Boolean
)