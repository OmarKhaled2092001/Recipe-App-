package com.example.recipeapp.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeapp.adapters.RecipeAdapter
import com.example.recipeapp.data.local.AppDatabase
import com.example.recipeapp.data.repository.MealRepository
import com.example.recipeapp.databinding.FragmentCategoryBinding
import com.example.recipeapp.ui.viewmodels.HomeViewModel
import com.example.recipeapp.ui.viewmodels.ViewModelFactory
import com.example.recipeapp.util.Resource

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var recipeAdapter: RecipeAdapter

    private var categoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryName = arguments?.getString("categoryName")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        val database = AppDatabase.getInstance(requireContext())
        val repository = MealRepository(database)
        val factory = ViewModelFactory(repository)
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCategoryTitle.text = categoryName ?: "Category"

        setupRecyclerView()
        observeMeals()

        categoryName?.let {
            homeViewModel.getMealsByCategory(it)
        }

        recipeAdapter.setOnItemClickListener { mealId ->
            val action = CategoryFragmentDirections.actionCategoryFragmentToRecipeDetailFragment(mealId)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter()
        binding.rvCategoryRecipes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recipeAdapter
        }
    }

    private fun observeMeals() {
        homeViewModel.mealsByCategory.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> binding.categoryProgressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.categoryProgressBar.visibility = View.GONE
                    resource.data?.let { recipeAdapter.differ.submitList(it) }
                }
                is Resource.Error -> {
                    binding.categoryProgressBar.visibility = View.GONE
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
