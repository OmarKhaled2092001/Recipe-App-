package com.example.recipeapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.data.models.Meal
import com.example.recipeapp.data.repository.MealRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: MealRepository) : ViewModel() {

    val favoriteMeals: LiveData<List<Meal>> = repository.getFavoriteMeals().asLiveData()


    fun deleteMeal(meal: Meal) = viewModelScope.launch {
        repository.deleteMeal(meal)
    }


    fun upsertMeal(meal: Meal) = viewModelScope.launch {
        repository.upsertMeal(meal)
    }

}