package com.stadio.textbookstore.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.stadio.textbookstore.R
import com.stadio.textbookstore.data.Book
import com.stadio.textbookstore.data.User
import com.stadio.textbookstore.databinding.FragmentBookDetailsBinding
import com.stadio.textbookstore.viewmodel.BookListViewModel
import com.stadio.textbookstore.viewmodel.MessagesViewModel
import com.stadio.textbookstore.viewmodel.UserViewModel

class BookDetailsFragment : Fragment() {

    private var _binding: FragmentBookDetailsBinding? = null
    private val binding get() = _binding!!

    private val bookViewModel: BookListViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val messagesViewModel: MessagesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.askSellerButton.setOnClickListener {
            val currentUser = userViewModel.currentUser.value
            val seller = bookViewModel.selectedSeller.value
            if (currentUser == null || seller == null) return@setOnClickListener

            if (currentUser.id == seller.id) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.thread_cant_message_self),
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            messagesViewModel.openThread(currentUser.id, seller.id)
            findNavController().navigate(R.id.action_details_to_thread)
        }

        //see selected book and populate screen
        bookViewModel.selectedBook.observe(viewLifecycleOwner) { book ->
            if (book == null) {
                // Defensive: if for some reason no book is selected, go back.
                findNavController().popBackStack()
                return@observe
            }
            populateBook(book)
        }

        //see seller of selected book
        bookViewModel.selectedSeller.observe(viewLifecycleOwner) { seller ->
            populateSeller(seller)
        }
    }

    private fun populateBook(book: Book) {
        binding.titleText.text = book.title
        binding.authorText.text = getString(R.string.details_author_format, book.author)
        binding.isbnText.text = getString(R.string.details_isbn_format, book.isbn)
        binding.priceText.text = getString(R.string.price_format, book.price)
        binding.conditionChip.text = getString(R.string.condition_label, book.condition)
        binding.descriptionText.text = book.description
    }

    private fun populateSeller(seller: User?) {
        if (seller == null) {
            binding.sellerNameText.text = "Unknown seller"
            binding.sellerDetailsText.text = ""
            return
        }
        binding.sellerNameText.text = seller.fullName
        binding.sellerDetailsText.text = getString(
            R.string.details_seller_format,
            seller.institution,
            seller.course,
            seller.studentStatus
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}