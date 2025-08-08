package com.example.recipeapp.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.R
import com.example.recipeapp.ui.home.RecipeActivity
import com.example.recipeapp.util.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycleScope.launch {
            delay(2000)

            val isLoggedIn = SessionManager.isLoggedIn(requireContext())

            if (isLoggedIn) {
                val intent = Intent(requireActivity(), RecipeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
}