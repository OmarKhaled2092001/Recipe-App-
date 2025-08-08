package com.example.recipeapp.ui.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.R
import com.example.recipeapp.data.local.AppDatabase
import com.example.recipeapp.data.repository.MealRepository
import com.example.recipeapp.databinding.FragmentLoginBinding
import com.example.recipeapp.ui.home.RecipeActivity
import com.example.recipeapp.ui.viewmodels.AuthStatus
import com.example.recipeapp.ui.viewmodels.AuthViewModel
import com.example.recipeapp.ui.viewmodels.ViewModelFactory
import com.example.recipeapp.util.SessionManager

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val database = AppDatabase.getInstance(requireContext())
        val repository = MealRepository(database)
        val factory = ViewModelFactory(repository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.login(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.loginStatus.observe(viewLifecycleOwner) { (status, message) ->
            when (status) {
                AuthStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.loginBtn.isEnabled = false
                }
                AuthStatus.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    binding.loginBtn.isEnabled = true

                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    SessionManager.setLoggedIn(requireContext(), true)
                    val intent = Intent(requireActivity(), RecipeActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                AuthStatus.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.loginBtn.isEnabled = true

                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.loginBtn.isEnabled = true
                }
            }
        }

        val fullText = getString(R.string.don_t_have_any_account_sign_up)
        val signUpText = "Sign Up"
        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#f67669")
                ds.isUnderlineText = true
            }
        }

        val startIndex = fullText.indexOf(signUpText)
        val endIndex = startIndex + signUpText.length

        if (startIndex != -1) {
            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.signUpText.text = spannableString
        binding.signUpText.movementMethod = LinkMovementMethod.getInstance()
        binding.signUpText.highlightColor = Color.TRANSPARENT

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}