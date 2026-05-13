package com.stadio.textbookstore.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.stadio.textbookstore.R
import com.stadio.textbookstore.databinding.FragmentLoginBinding
import com.stadio.textbookstore.viewmodel.UserViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            attemptLogin()
        }

        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        binding.forgotPasswordLink.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_forgot)
        }
    }

    private fun attemptLogin() {
        val email = binding.emailInput.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInput.text?.toString().orEmpty()

        //Clear previous errors
        binding.emailInputLayout.error = null
        binding.passwordInputLayout.error = null

        //validation
        if (email.isEmpty()) {
            binding.emailInputLayout.error = getString(com.stadio.textbookstore.R.string.error_email_required)
            return
        }
        if (password.isEmpty()) {
            binding.passwordInputLayout.error = getString(com.stadio.textbookstore.R.string.error_password_required)
            return
        }

        //login attempt
        val success = userViewModel.login(email, password)

        if (success) {
            val name = userViewModel.currentUser.value?.fullName ?: "user"
            Toast.makeText(requireContext(), "Welcome, $name!", Toast.LENGTH_SHORT).show()
        } else {
            val errorMessage = userViewModel.authError.value ?: "Login failed."
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}