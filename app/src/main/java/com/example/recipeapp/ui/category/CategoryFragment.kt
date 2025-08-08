package com.example.recipeapp.ui.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeapp.R
import com.example.recipeapp.data.adapters.RecipeAdapter
import com.example.recipeapp.data.local.AppDatabase
import com.example.recipeapp.data.repository.MealRepository
import com.example.recipeapp.data.util.Resource
import com.example.recipeapp.databinding.FragmentCategoryBinding
import com.example.recipeapp.ui.viewmodels.HomeViewModel
import com.example.recipeapp.ui.viewmodels.ViewModelFactory

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var recipesAdapter: RecipeAdapter
//    private val args: CategoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        val database = AppDatabase.getInstance(requireContext())
        val repository = MealRepository(database)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val categoryName = args.categoryName

        setupRecyclerView()

        observeCategoryMeals()

//        binding.tvCategoryTitle.text = categoryName

//        viewModel.searchMeals(categoryName)

        recipesAdapter.setOnItemClickListener { meal ->
            Toast.makeText(context, meal.strMeal, Toast.LENGTH_SHORT).show()
            // navigate to RecipeDetailsFragment
        }
    }

    private fun setupRecyclerView() {
        recipesAdapter = RecipeAdapter()
        binding.rvCategoryRecipes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recipesAdapter
        }
    }

    private fun observeCategoryMeals() {
        viewModel.searchedMeals.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.categoryProgressBar.visibility = View.VISIBLE
                    binding.rvCategoryRecipes.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.categoryProgressBar.visibility = View.GONE
                    binding.rvCategoryRecipes.visibility = View.VISIBLE
                    resource.data?.let { response ->
                        if (response.meals != null) {
                            recipesAdapter.differ.submitList(response.meals)
                        } else {
                            recipesAdapter.differ.submitList(emptyList())
//                            Toast.makeText(context, "No recipes found for ${args.categoryName}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                is Resource.Error -> {
                    binding.categoryProgressBar.visibility = View.GONE
                    binding.rvCategoryRecipes.visibility = View.VISIBLE
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}