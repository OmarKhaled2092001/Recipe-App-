package com.example.recipeapp.ui.recipedetail

import androidx.lifecycle.*
import com.example.recipeapp.data.models.Meal
import com.example.recipeapp.data.repository.MealRepository
import kotlinx.coroutines.launch

sealed class MealDetailState {
    object Loading : MealDetailState()
    data class Success(val meal: Meal) : MealDetailState()
    data class Error(val message: String) : MealDetailState()
}

class RecipeDetailViewModel(private val repository: MealRepository) : ViewModel() {

    private val _mealDetailState = MutableLiveData<MealDetailState>()
    val mealDetailState: LiveData<MealDetailState> = _mealDetailState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun fetchMealDetails(id: String) {
        _mealDetailState.postValue(MealDetailState.Loading)

        viewModelScope.launch {
            repository.getMealById(id).collect { cachedMeal ->
                if (cachedMeal != null) {
                    _mealDetailState.postValue(MealDetailState.Success(cachedMeal))
                    _isFavorite.postValue(true)
                } else {
                    _isFavorite.postValue(false)
                }
            }
        }

        viewModelScope.launch {
            try {
                val response = repository.getMealDetails(id)
                val meal = response.meals.firstOrNull()
                if (meal != null) {
                    _mealDetailState.postValue(MealDetailState.Success(meal))
                } else {
                    _mealDetailState.postValue(MealDetailState.Error("Meal details not found"))
                }
            } catch (e: Exception) {
                _mealDetailState.postValue(MealDetailState.Error("Error: ${e.localizedMessage ?: e.message}"))
            }
        }
    }

    fun toggleFavorite(meal: Meal) {
        viewModelScope.launch {
            if (_isFavorite.value == true) {
                repository.deleteMeal(meal)
                _isFavorite.postValue(false)
            } else {
                repository.upsertMeal(meal)
                _isFavorite.postValue(true)
            }
        }
    }
}
