package com.example.recipeapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.recipeapp.data.models.Meal
import com.example.recipeapp.data.repository.MealRepository

class FavoritesViewModel(private val repository: MealRepository) : ViewModel() {

    val favoriteMeals: LiveData<List<Meal>> = repository.getFavoriteMeals().asLiveData()

}