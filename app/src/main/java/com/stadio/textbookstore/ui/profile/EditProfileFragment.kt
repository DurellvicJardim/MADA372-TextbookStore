package com.stadio.textbookstore.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.stadio.textbookstore.R
import com.stadio.textbookstore.databinding.FragmentEditProfileBinding
import com.stadio.textbookstore.viewmodel.UserViewModel

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()

    private var pendingProfilePicUri: String? = null

    private val pickProfilePic = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pendingProfilePicUri = uri.toString()
            binding.editAvatar.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropdowns()
        prefillForm()

        binding.topBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        //Both avatar and label open the picker
        val openPicker = View.OnClickListener {
            pickProfilePic.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
        binding.editAvatar.setOnClickListener(openPicker)
        binding.changePhotoLabel.setOnClickListener(openPicker)

        binding.saveButton.setOnClickListener {
            attemptSave()
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

    private fun prefillForm() {
        val user = userViewModel.currentUser.value
        if (user == null) {
            findNavController().popBackStack()
            return
        }
        binding.nameInput.setText(user.fullName)
        binding.emailInput.setText(user.email)
        binding.institutionInput.setText(user.institution, false)
        binding.courseInput.setText(user.course)
        binding.statusInput.setText(user.studentStatus, false)

        //Initialise pending URI with what user has and display it
        pendingProfilePicUri = user.profilePicUri
        if (user.profilePicUri != null) {
            binding.editAvatar.setImageURI(Uri.parse(user.profilePicUri))
        }
    }

    private fun attemptSave() {
        val fullName = binding.nameInput.text?.toString()?.trim().orEmpty()
        val institution = binding.institutionInput.text?.toString()?.trim().orEmpty()
        val course = binding.courseInput.text?.toString()?.trim().orEmpty()
        val status = binding.statusInput.text?.toString()?.trim().orEmpty()

        binding.nameInputLayout.error = null
        binding.institutionInputLayout.error = null
        binding.courseInputLayout.error = null
        binding.statusInputLayout.error = null

        if (fullName.isEmpty()) {
            binding.nameInputLayout.error = getString(R.string.error_name_required)
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

        val current = userViewModel.currentUser.value ?: return
        val updated = current.copy(
            fullName = fullName,
            institution = institution,
            course = course,
            studentStatus = status,
            profilePicUri = pendingProfilePicUri
        )

        userViewModel.updateProfile(updated)

        Toast.makeText(
            requireContext(),
            getString(R.string.edit_save_success),
            Toast.LENGTH_SHORT
        ).show()

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}