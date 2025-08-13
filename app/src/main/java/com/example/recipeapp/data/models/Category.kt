package com.example.recipeapp.data.models

data class Category(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String,
    var isFavorite: Boolean = false,
)