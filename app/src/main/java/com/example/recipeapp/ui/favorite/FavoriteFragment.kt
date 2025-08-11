package com.example.recipeapp.ui.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.adapters.RecipeAdapter
import com.example.recipeapp.data.local.AppDatabase
import com.example.recipeapp.data.models.Meal
import com.example.recipeapp.data.models.MealWithFavoriteStatus
import com.example.recipeapp.data.repository.MealRepository
import com.example.recipeapp.databinding.FragmentFavoriteBinding
import com.example.recipeapp.ui.viewmodels.FavoritesViewModel
import com.example.recipeapp.ui.viewmodels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var favoritesAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        val database = AppDatabase.getInstance(requireContext())
        val repository = MealRepository(database)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(FavoritesViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeFavorites()
        setupSwipeToDelete(view)
    }

    private fun setupRecyclerView() {
        favoritesAdapter = RecipeAdapter()
        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoritesAdapter
        }

        favoritesAdapter.setOnItemClickListener { meal ->
            val action = FavoriteFragmentDirections.actionFavoriteFragmentToRecipeDetailFragment(meal)
            findNavController().navigate(action)
        }

        favoritesAdapter.setOnAddFavoriteClickListener { }

        favoritesAdapter.setOnDeleteFavoriteClickListener { meal ->
            deleteRecipeWithUndo(meal)
        }
    }

    private fun observeFavorites() {
        viewModel.favoriteMeals.observe(viewLifecycleOwner) { meals ->
            if (meals.isEmpty()) {
                binding.tvNoFavorites.visibility = View.VISIBLE
                binding.rvFavorites.visibility = View.GONE
            } else {
                binding.tvNoFavorites.visibility = View.GONE
                binding.rvFavorites.visibility = View.VISIBLE
            }

            val mealsWithStatus = meals.map { meal ->
                MealWithFavoriteStatus(meal = meal, isFavorite = true)
            }

            favoritesAdapter.differ.submitList(mealsWithStatus)
        }
    }

    private fun setupSwipeToDelete(view: View) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val mealWithStatusToDelete = favoritesAdapter.differ.currentList[position]

                deleteRecipeWithUndo(mealWithStatusToDelete.meal)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvFavorites)
    }

    private fun deleteRecipeWithUndo(meal: Meal) {
        viewModel.deleteMeal(meal)
        Snackbar.make(requireView(), "Recipe deleted", Snackbar.LENGTH_LONG).apply {
            setAction("Undo") {
                viewModel.upsertMeal(meal)
            }
            show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}