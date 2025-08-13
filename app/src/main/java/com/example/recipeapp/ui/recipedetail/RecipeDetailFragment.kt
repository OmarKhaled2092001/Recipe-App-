package com.example.recipeapp.ui.recipedetail

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.data.local.AppDatabase
import com.example.recipeapp.data.repository.MealRepository
import com.example.recipeapp.databinding.FragmentRecipeDetailBinding
import com.example.recipeapp.ui.viewmodels.ViewModelFactory

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecipeDetailViewModel
    private var currentMeal: com.example.recipeapp.data.models.Meal? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mealId = arguments?.let {
            RecipeDetailFragmentArgs.fromBundle(it).mealId
        } ?: ""

        val database = AppDatabase.getInstance(requireContext())
        val repository = MealRepository(database)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RecipeDetailViewModel::class.java]

        viewModel.fetchMealDetails(mealId)

        viewModel.mealDetailState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MealDetailState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.layoutContent.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                }
                is MealDetailState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.layoutContent.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                    currentMeal = state.meal
                    displayMealDetails(state.meal)
                }
                is MealDetailState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.layoutContent.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
                else -> {}
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFav ->
            val color = if (isFav) Color.RED else Color.GRAY
            binding.favIc.imageTintList = ColorStateList.valueOf(color)
        }

        binding.favIc.setOnClickListener {
            currentMeal?.let { meal ->
                viewModel.toggleFavorite(meal)
            }
        }

        binding.btnToggleInstructions.setOnClickListener {
            if (binding.tvInstructions.maxLines == 5) {
                binding.tvInstructions.maxLines = Integer.MAX_VALUE
                binding.btnToggleInstructions.text = getString(R.string.less)
            } else {
                binding.tvInstructions.maxLines = 5
                binding.btnToggleInstructions.text = getString(R.string.more)
            }
        }
    }

    private fun displayMealDetails(meal: com.example.recipeapp.data.models.Meal) {
        binding.tvRecipeTitle.text = meal.strMeal ?: "No Title"
        Glide.with(requireContext())
            .load(meal.strMealThumb)
            .into(binding.imageRecipe)

        binding.tvInstructions.text = meal.strInstructions ?: "No instructions available"

        val ingredientsList = mutableListOf<String>()
        for (i in 1..20) {
            val ingredient = meal.javaClass.getDeclaredField("strIngredient$i").apply { isAccessible = true }.get(meal) as? String
            val measure = meal.javaClass.getDeclaredField("strMeasure$i").apply { isAccessible = true }.get(meal) as? String

            if (!ingredient.isNullOrBlank() && ingredient != "null") {
                val item = if (!measure.isNullOrBlank() && measure != "null") "$measure $ingredient" else ingredient
                ingredientsList.add(item.trim())
            }
        }

        binding.layoutIngredients.removeAllViews()
        for (item in ingredientsList) {
            val tv = TextView(requireContext()).apply {
                text = "• $item"
                setTextColor(Color.BLACK)
                textSize = 14f
            }
            binding.layoutIngredients.addView(tv)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
