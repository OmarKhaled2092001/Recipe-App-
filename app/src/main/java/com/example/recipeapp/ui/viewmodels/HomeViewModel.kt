package com.example.recipeapp.ui.viewmodels

import MealsResponse
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.data.models.CategoriesResponse
import com.example.recipeapp.data.repository.MealRepository
import com.example.recipeapp.util.Resource
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MealRepository) : ViewModel() {


    val categories: MutableLiveData<Resource<CategoriesResponse>> = MutableLiveData()
    val searchedMeals: MutableLiveData<Resource<MealsResponse>> = MutableLiveData()


    fun getCategories() {
        viewModelScope.launch {
            categories.postValue(Resource.Loading())
            try {
                val response = repository.getCategories()
                categories.postValue(Resource.Success(response))
            } catch (e: Exception) {
                categories.postValue(Resource.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }
    fun searchMeals(searchQuery: String) {
        viewModelScope.launch {
            searchedMeals.postValue(Resource.Loading())
            try {
                val response = repository.searchMeals(searchQuery)
                searchedMeals.postValue(Resource.Success(response))
            } catch (e: Exception) {
                searchedMeals.postValue(Resource.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }
}