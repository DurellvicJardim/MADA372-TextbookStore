package com.stadio.textbookstore.ui.sell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.stadio.textbookstore.R
import com.stadio.textbookstore.databinding.FragmentSellBinding
import com.stadio.textbookstore.viewmodel.BookListViewModel
import com.stadio.textbookstore.viewmodel.UserViewModel

class SellFragment : Fragment() {

    private var _binding: FragmentSellBinding? = null
    private val binding get() = _binding!!

    private val bookViewModel: BookListViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupConditionDropdown()

        binding.listBookButton.setOnClickListener {
            attemptListBook()
        }
    }

    private fun setupConditionDropdown() {
        val conditions = resources.getStringArray(R.array.conditions)
        binding.conditionInput.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, conditions)
        )
    }

    private fun attemptListBook() {
        val title = binding.titleInput.text?.toString()?.trim().orEmpty()
        val author = binding.authorInput.text?.toString()?.trim().orEmpty()
        val isbn = binding.isbnInput.text?.toString()?.trim().orEmpty()
        val priceText = binding.priceInput.text?.toString()?.trim().orEmpty()
        val condition = binding.conditionInput.text?.toString()?.trim().orEmpty()
        val description = binding.descriptionInput.text?.toString()?.trim().orEmpty()

        //Clear errors
        binding.titleInputLayout.error = null
        binding.authorInputLayout.error = null
        binding.isbnInputLayout.error = null
        binding.priceInputLayout.error = null
        binding.conditionInputLayout.error = null

        //Validate
        if (title.isEmpty()) {
            binding.titleInputLayout.error = getString(R.string.error_title_required)
            return
        }
        if (author.isEmpty()) {
            binding.authorInputLayout.error = getString(R.string.error_author_required)
            return
        }
        if (isbn.isEmpty()) {
            binding.isbnInputLayout.error = getString(R.string.error_isbn_required)
            return
        }
        val price = priceText.toDoubleOrNull()
        if (price == null || price <= 0.0) {
            binding.priceInputLayout.error = getString(R.string.error_price_invalid)
            return
        }
        if (condition.isEmpty()) {
            binding.conditionInputLayout.error = getString(R.string.error_condition_required)
            return
        }

        //Need to know who's listing + must be logged in
        val currentUser = userViewModel.currentUser.value
        if (currentUser == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.sell_not_logged_in),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        //Add book via the shared ViewModel
        bookViewModel.addBook(
            title = title,
            author = author,
            isbn = isbn,
            price = price,
            condition = condition,
            description = description.ifEmpty { "No description provided." },
            sellerId = currentUser.id
        )

        Toast.makeText(
            requireContext(),
            getString(R.string.sell_success),
            Toast.LENGTH_SHORT
        ).show()

        //Clear form
        binding.titleInput.text = null
        binding.authorInput.text = null
        binding.isbnInput.text = null
        binding.priceInput.text = null
        binding.conditionInput.text = null
        binding.descriptionInput.text = null

        //Send user to Home to see their listing in the feed
        findNavController().navigate(R.id.homeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}