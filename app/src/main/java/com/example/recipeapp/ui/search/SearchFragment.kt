package com.example.recipeapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeapp.R
import com.example.recipeapp.adapters.RecipeAdapter
import com.example.recipeapp.data.local.AppDatabase
import com.example.recipeapp.data.repository.MealRepository
import com.example.recipeapp.util.Resource
import com.example.recipeapp.databinding.FragmentSearchBinding
import com.example.recipeapp.ui.viewmodels.HomeViewModel
import com.example.recipeapp.ui.viewmodels.ViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var searchAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        val database = AppDatabase.getInstance(requireContext())
        val repository = MealRepository(database)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeSearchResults()

        var searchJob: Job? = null
        binding.etSearchBox.addTextChangedListener { editable ->
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(500L)
                editable?.let {
                    if (it.toString().isNotEmpty()) {
                        viewModel.searchMeals(it.toString())
                    } else {
                        searchAdapter.differ.submitList(emptyList())
                    }
                }
            }
        }

        searchAdapter.setOnItemClickListener { meal ->
            val action = SearchFragmentDirections.actionSearchFragmentToRecipeDetailFragment(meal)
            findNavController().navigate(action)
        }

        searchAdapter.setOnAddFavoriteClickListener { meal ->
            viewModel.upsertMeal(meal)
            Toast.makeText(context, "${meal.strMeal} saved", Toast.LENGTH_SHORT).show()
        }

        searchAdapter.setOnDeleteFavoriteClickListener { meal ->
            viewModel.deleteMeal(meal)
            Toast.makeText(context, "${meal.strMeal} removed", Toast.LENGTH_SHORT).show()
        }

        val noInternetBinding = binding.noInternetView

        noInternetBinding.btnRetry.setOnClickListener {
            viewModel.getCategories()
        }

        noInternetBinding.tvOpenSaved.setOnClickListener {
            findNavController().navigate(R.id.favoriteFragment)
        }
    }


    private fun setupRecyclerView() {
        searchAdapter = RecipeAdapter()
        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    private fun observeSearchResults() {
        viewModel.searchedMeals.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.noInternetView.root.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.noInternetView.root.visibility = View.GONE
                    resource.data?.let { mealsWithStatusList ->
                        searchAdapter.differ.submitList(mealsWithStatusList)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    if (resource.message == "NO_INTERNET_CONNECTION") {
                        binding.noInternetView.root.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}