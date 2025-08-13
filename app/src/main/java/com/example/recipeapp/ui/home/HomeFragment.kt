package com.example.recipeapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipeapp.R
import com.example.recipeapp.adapters.CategoryAdapter
import com.example.recipeapp.data.local.AppDatabase
import com.example.recipeapp.data.repository.MealRepository
import com.example.recipeapp.databinding.FragmentHomeBinding
import com.example.recipeapp.ui.viewmodels.HomeViewModel
import com.example.recipeapp.ui.viewmodels.ViewModelFactory
import com.example.recipeapp.util.GridSpacingItemDecoration
import com.example.recipeapp.util.Resource

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val database = AppDatabase.getInstance(requireContext())
        val repository = MealRepository(database)
        val factory = ViewModelFactory(repository)
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeCategories()

        homeViewModel.getCategories()

        categoryAdapter.setOnItemClickListener { category ->
            val action = HomeFragmentDirections.actionHomeFragmentToCategoryFragment(category.strCategory)
            findNavController().navigate(action)
        }

        val noInternetBinding = binding.noInternetView

        noInternetBinding.btnRetry.setOnClickListener {
            homeViewModel.getCategories()
        }

        noInternetBinding.tvOpenSaved.setOnClickListener {
            findNavController().navigate(R.id.favoriteFragment)
        }
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter()

        val recyclerView = binding.rvCategoriesHorizontal
        val screenWidthDp = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val desiredColumnWidthDp = 180
        val spanCount = maxOf(1, (screenWidthDp / desiredColumnWidthDp).toInt())

        val spacingInPixels = (16 * resources.displayMetrics.density).toInt()

        val itemDecoration = GridSpacingItemDecoration(
            spanCount,
            spacingInPixels,
            spacingInPixels
        )
        recyclerView.addItemDecoration(itemDecoration)

        recyclerView.apply {
            layoutManager = GridLayoutManager(context, spanCount, GridLayoutManager.VERTICAL, false)
            adapter = categoryAdapter
        }
    }

    private fun observeCategories() {
        homeViewModel.categories.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.homeProgressBar.visibility = View.VISIBLE
                    binding.noInternetView.root.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.homeProgressBar.visibility = View.GONE
                    binding.noInternetView.root.visibility = View.GONE
                    resource.data?.let { response ->
                        categoryAdapter.differ.submitList(response.categories)
                    }
                }
                is Resource.Error -> {
                    binding.homeProgressBar.visibility = View.GONE
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