package com.example.recipeapp.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.data.models.Category
import com.example.recipeapp.databinding.CategoryItemBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoryAdapter(
    private val shareFab: FloatingActionButton? = null // هنمرر الـ FAB من الـ Fragment
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.idCategory == newItem.idCategory
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            CategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = differ.currentList[position]

        holder.binding.apply {
            txtCategory.text = category.strCategory
            Glide.with(holder.itemView)
                .load(category.strCategoryThumb)
                .into(categoryImage)
        }

        // هنا هنغير لون الـ FAB حسب حالة الـ Favorite
        category.isFavorite?.let { isFav ->
            shareFab?.imageTintList = if (isFav) {
                ColorStateList.valueOf(Color.RED)
            } else {
                ColorStateList.valueOf(Color.GRAY)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(category) }
        }
    }

    private var onItemClickListener: ((Category) -> Unit)? = null

    fun setOnItemClickListener(listener: (Category) -> Unit) {
        onItemClickListener = listener
    }
}