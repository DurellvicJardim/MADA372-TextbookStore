package com.stadio.textbookstore.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.stadio.textbookstore.R
import com.stadio.textbookstore.databinding.FragmentForgotPasswordBinding
import com.stadio.textbookstore.viewmodel.UserViewModel

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resetButton.setOnClickListener { attemptReset() }
        binding.backToLoginLink.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun attemptReset() {
        val email = binding.emailInput.text?.toString()?.trim().orEmpty()
        val newPassword = binding.newPasswordInput.text?.toString().orEmpty()
        val confirmPassword = binding.confirmPasswordInput.text?.toString().orEmpty()

        // Clear existing errors
        binding.emailInputLayout.error = null
        binding.newPasswordInputLayout.error = null
        binding.confirmPasswordInputLayout.error = null

        // Validate
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = getString(R.string.error_email_invalid)
            return
        }
        if (newPassword.length < 6) {
            binding.newPasswordInputLayout.error = getString(R.string.error_password_short)
            return
        }
        if (confirmPassword != newPassword) {
            binding.confirmPasswordInputLayout.error = getString(R.string.error_passwords_dont_match)
            return
        }

        val success = userViewModel.resetPassword(email, newPassword)
        if (success) {
            Toast.makeText(
                requireContext(),
                getString(R.string.success_password_reset),
                Toast.LENGTH_LONG
            ).show()
            findNavController().popBackStack()
        } else {
            binding.emailInputLayout.error = getString(R.string.error_email_not_found)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}