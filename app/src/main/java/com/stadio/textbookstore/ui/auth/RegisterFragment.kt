package com.stadio.textbookstore.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.stadio.textbookstore.R
import com.stadio.textbookstore.databinding.FragmentRegisterBinding
import com.stadio.textbookstore.viewmodel.UserViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropdowns()

        binding.registerButton.setOnClickListener { attemptRegister() }
        binding.loginLink.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupDropdowns() {
        val institutions = resources.getStringArray(R.array.institutions)
        binding.institutionInput.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, institutions)
        )

        val statuses = resources.getStringArray(R.array.student_statuses)
        binding.statusInput.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, statuses)
        )
    }

    private fun attemptRegister() {
        val fullName = binding.nameInput.text?.toString()?.trim().orEmpty()
        val email = binding.emailInput.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInput.text?.toString().orEmpty()
        val institution = binding.institutionInput.text?.toString()?.trim().orEmpty()
        val course = binding.courseInput.text?.toString()?.trim().orEmpty()
        val status = binding.statusInput.text?.toString()?.trim().orEmpty()

        // Clear all errors
        binding.nameInputLayout.error = null
        binding.emailInputLayout.error = null
        binding.passwordInputLayout.error = null
        binding.institutionInputLayout.error = null
        binding.courseInputLayout.error = null
        binding.statusInputLayout.error = null

        // Validate in order
        if (fullName.isEmpty()) {
            binding.nameInputLayout.error = getString(R.string.error_name_required)
            return
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = getString(R.string.error_email_invalid)
            return
        }
        if (password.length < 6) {
            binding.passwordInputLayout.error = getString(R.string.error_password_short)
            return
        }
        if (institution.isEmpty()) {
            binding.institutionInputLayout.error = getString(R.string.error_institution_required)
            return
        }
        if (course.isEmpty()) {
            binding.courseInputLayout.error = getString(R.string.error_course_required)
            return
        }
        if (status.isEmpty()) {
            binding.statusInputLayout.error = getString(R.string.error_status_required)
            return
        }

        val success = userViewModel.register(fullName, email, password, institution, course, status)
        if (success) {
            Toast.makeText(requireContext(), "Welcome, $fullName!", Toast.LENGTH_SHORT).show()
            // Phase 5 will navigate to Home. For now, back to Login.
            findNavController().popBackStack()
        } else {
            val errorMessage = userViewModel.authError.value ?: "Registration failed."
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}