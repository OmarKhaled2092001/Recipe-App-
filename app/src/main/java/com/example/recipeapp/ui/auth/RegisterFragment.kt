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
import com.example.recipeapp.databinding.FragmentRegisterBinding
import com.example.recipeapp.ui.home.RecipeActivity
import com.example.recipeapp.ui.viewmodels.AuthStatus
import com.example.recipeapp.ui.viewmodels.AuthViewModel
import com.example.recipeapp.ui.viewmodels.ViewModelFactory
import com.example.recipeapp.util.SessionManager

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val database = AppDatabase.getInstance(requireContext())
        val repository = MealRepository(database)
        val factory = ViewModelFactory(repository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerBtn.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.register(username, email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.registrationStatus.observe(viewLifecycleOwner) { (status, message) ->
            when (status) {
                AuthStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.registerBtn.isEnabled = false
                }
                AuthStatus.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    binding.registerBtn.isEnabled = true

                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    SessionManager.setLoggedIn(requireContext(), true)
                    val intent = Intent(requireActivity(), RecipeActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                AuthStatus.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.registerBtn.isEnabled = true

                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.registerBtn.isEnabled = true
                }
            }
        }

        val fullText = getString(R.string.already_have_an_account_sign_in)
        val signInText = "Sign in"
        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#f67669")
                ds.isUnderlineText = true
            }
        }

        val startIndex = fullText.indexOf(signInText)
        val endIndex = startIndex + signInText.length

        if (startIndex != -1) {
            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.signInText.text = spannableString
        binding.signInText.movementMethod = LinkMovementMethod.getInstance()
        binding.signInText.highlightColor = Color.TRANSPARENT
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}