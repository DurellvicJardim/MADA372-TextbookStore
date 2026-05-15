package com.stadio.textbookstore.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.stadio.textbookstore.R
import com.stadio.textbookstore.data.User
import com.stadio.textbookstore.databinding.FragmentProfileBinding
import com.stadio.textbookstore.viewmodel.UserViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user ?: return@observe
            populateUser(user)
        }

        binding.editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_edit)
        }

        binding.logoutButton.setOnClickListener {
            userViewModel.logout()
            Toast.makeText(
                requireContext(),
                getString(R.string.profile_logout_confirmed),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_profile_to_login)
        }
    }

    private fun populateUser(user: User) {
        binding.nameText.text = user.fullName
        binding.emailText.text = user.email
        binding.institutionValue.text = user.institution
        binding.courseValue.text = user.course
        binding.statusValue.text = user.studentStatus

        //Show profile picture if user has one set otherwise brown background shows
        if (user.profilePicUri != null) {
            binding.profilePicture.setImageURI(android.net.Uri.parse(user.profilePicUri))
        } else {
            binding.profilePicture.setImageURI(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}