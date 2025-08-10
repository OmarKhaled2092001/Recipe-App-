import androidx.lifecycle.*
import com.example.recipeapp.data.models.Meal
import com.example.recipeapp.data.repository.MealRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed class MealDetailState {
    object Loading : MealDetailState()
    data class Success(val meal: Meal) : MealDetailState()
    data class Error(val message: String) : MealDetailState()
}

class RecipeDetailViewModel(private val repository: MealRepository) : ViewModel() {

    private val _mealDetailState = MutableLiveData<MealDetailState>()
    val mealDetailState: LiveData<MealDetailState> = _mealDetailState

    fun fetchMealDetails(id: String) {
        _mealDetailState.postValue(MealDetailState.Loading)

        viewModelScope.launch {

            repository.getMealById(id).collect { cachedMeal ->
                if (cachedMeal != null) {

                    _mealDetailState.postValue(MealDetailState.Success(cachedMeal))
                }
            }
        }

        viewModelScope.launch {
            try {
                // from api
                val response = repository.getMealDetails(id)
                val meal = response.meals.firstOrNull()
                if (meal != null) {
                    // save in room
                    repository.upsertMeal(meal)
                    // save status
                    _mealDetailState.postValue(MealDetailState.Success(meal))
                } else {
                    _mealDetailState.postValue(MealDetailState.Error("Meal details not found"))
                }
            } catch (e: Exception) {
                // Error handling
                _mealDetailState.postValue(MealDetailState.Error("Error: ${e.localizedMessage ?: e.message}"))
            }
        }
    }
}
