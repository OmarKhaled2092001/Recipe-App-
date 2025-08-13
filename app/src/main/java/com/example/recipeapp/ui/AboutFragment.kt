package com.example.recipeapp.ui

import com.example.recipeapp.adapters.CreatorsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.data.models.Creator

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        val description = view.findViewById<TextView>(R.id.Description)
        description.text=getString(R.string.recipe_app_helps_you_discover_cook_and_enjoy_delicious_meals_from_around_the_world_search_watch_and_save_your_favorite_recipes_in_a_clean_and_friendly_interface_cooking_made_simple_and_fun)
        val Creators = view.findViewById<RecyclerView>(R.id.Creators)
        Creators.layoutManager = LinearLayoutManager(requireContext())

        val creatorsList = listOf(
            Creator(
                "Omar Khaled Jaafar",
                "omark5787@gmail.com",
                "https://www.linkedin.com/in/omar-khaled-754122225/",
                "https://github.com/OmarKhaled2092001",

            ),
            Creator(
                "Rawan Hassan Fathy",
                "rawanhassan1012@gmail.com",
                "https://www.linkedin.com/in/rawannhassan",
                " https://github.com/rawanhassan55"
            ),
            Creator(
                "Amany Mohamed",
                "amanymohamedmorsy23@gmail.com",
                "https://www.linkedin.com/in/amany-mohamed-morsy-826553297?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app",
                "https://github.com/Amanymohammad"
            ),
            Creator(
                "Hassan Ahmed Mohamed ",
                "elomdahassan3@gmail.com",
                "https://www.linkedin.com/in/hassan-ahmed-40952a37a",
                "https://github.com/HassanAhmedMohamed"
            )
        )

        Creators.adapter = CreatorsAdapter(creatorsList)

        return view
    }
}