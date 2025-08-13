package com.example.recipeapp.ui.viewmodels

import MealsResponse
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.data.models.CategoriesResponse
import com.example.recipeapp.data.models.Meal
import com.example.recipeapp.data.models.MealWithFavoriteStatus
import com.example.recipeapp.data.repository.MealRepository
import com.example.recipeapp.util.Resource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MealRepository) : ViewModel() {

    val categories: MutableLiveData<Resource<CategoriesResponse>> = MutableLiveData()
    val searchedMeals: MutableLiveData<Resource<List<MealWithFavoriteStatus>>> = MutableLiveData()
    val mealsByCategory: MutableLiveData<Resource<List<MealWithFavoriteStatus>>> = MutableLiveData()

    private var lastSearchQuery: String? = null

    fun getCategories() {
        viewModelScope.launch {
            categories.postValue(Resource.Loading())
            try {
                val response = repository.getCategories()
                categories.postValue(Resource.Success(response))
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is java.net.UnknownHostException, is java.io.IOException -> "NO_INTERNET_CONNECTION"
                    else -> e.message ?: "An unknown error occurred"
                }
                categories.postValue(Resource.Error(errorMessage))
            }
        }
    }

    fun searchMeals(searchQuery: String) {
        lastSearchQuery = searchQuery

        viewModelScope.launch {
            searchedMeals.postValue(Resource.Loading())
            try {
                val favoriteMeals = repository.getFavoriteMeals().first()
                val favoriteIds = favoriteMeals.map { it.idMeal }.toSet()
                val response = repository.searchMeals(searchQuery)
                val mealsWithStatus = response.meals.map { mealFromApi ->
                    MealWithFavoriteStatus(
                        meal = mealFromApi,
                        isFavorite = favoriteIds.contains(mealFromApi.idMeal)
                    )
                }
                searchedMeals.postValue(Resource.Success(mealsWithStatus))
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is java.net.UnknownHostException, is java.io.IOException -> "NO_INTERNET_CONNECTION"
                    else -> e.message ?: "An unknown error occurred"
                }
                searchedMeals.postValue(Resource.Error(errorMessage))
            }
        }
    }

    fun getMealsByCategory(categoryName: String) {
        viewModelScope.launch {
            mealsByCategory.postValue(Resource.Loading())
            try {
                val favoriteMeals = repository.getFavoriteMeals().first()
                val favoriteIds = favoriteMeals.map { it.idMeal }.toSet()
                val response = repository.getMealsByCategory(categoryName)
                val mealsWithStatus = response.meals.map { mealFromApi ->
                    MealWithFavoriteStatus(
                        meal = mealFromApi,
                        isFavorite = favoriteIds.contains(mealFromApi.idMeal)
                    )
                }
                mealsByCategory.postValue(Resource.Success(mealsWithStatus))
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is java.net.UnknownHostException, is java.io.IOException -> "NO_INTERNET_CONNECTION"
                    else -> e.message ?: "An unknown error occurred"
                }
                mealsByCategory.postValue(Resource.Error(errorMessage))
            }
        }
    }

    fun upsertMeal(meal: Meal) = viewModelScope.launch {
        repository.upsertMeal(meal)
        lastSearchQuery?.let { searchMeals(it) }
    }

    fun deleteMeal(meal: Meal) = viewModelScope.launch {
        repository.deleteMeal(meal)
        lastSearchQuery?.let { searchMeals(it) }
    }
}
