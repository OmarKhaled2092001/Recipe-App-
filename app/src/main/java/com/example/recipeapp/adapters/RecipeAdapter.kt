package com.example.recipeapp.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.data.models.Meal
import com.example.recipeapp.data.models.MealWithFavoriteStatus
import com.example.recipeapp.databinding.RecipeItemBinding

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(val binding: RecipeItemBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<MealWithFavoriteStatus>() {
        override fun areItemsTheSame(oldItem: MealWithFavoriteStatus, newItem: MealWithFavoriteStatus): Boolean {
            return oldItem.meal.idMeal == newItem.meal.idMeal
        }

        override fun areContentsTheSame(oldItem: MealWithFavoriteStatus, newItem: MealWithFavoriteStatus): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(
            RecipeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val mealWithStatus = differ.currentList[position]
        val meal = mealWithStatus.meal

        holder.binding.apply {
            mealName.text = meal.strMeal

            Glide.with(holder.itemView)
                .load(meal.strMealThumb)
                .into(mealImage)

            if (mealWithStatus.isFavorite) {
                shareFab.imageTintList = ColorStateList.valueOf(Color.RED)
            } else {
                shareFab.imageTintList = ColorStateList.valueOf(Color.GRAY)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(meal) }
        }

        holder.binding.shareFab.setOnClickListener {
            if (mealWithStatus.isFavorite) {
                onDeleteFavoriteClickListener?.let { it(meal) }
            } else {
                onAddFavoriteClickListener?.let { it(meal) }
            }
        }
    }

    private var onItemClickListener: ((Meal) -> Unit)? = null
    fun setOnItemClickListener(listener: (Meal) -> Unit) {
        onItemClickListener = listener
    }

    private var onAddFavoriteClickListener: ((Meal) -> Unit)? = null
    fun setOnAddFavoriteClickListener(listener: (Meal) -> Unit) {
        onAddFavoriteClickListener = listener
    }

    private var onDeleteFavoriteClickListener: ((Meal) -> Unit)? = null
    fun setOnDeleteFavoriteClickListener(listener: (Meal) -> Unit) {
        onDeleteFavoriteClickListener = listener
    }
}