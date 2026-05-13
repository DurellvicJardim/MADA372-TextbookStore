package com.stadio.textbookstore.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stadio.textbookstore.R
import com.stadio.textbookstore.databinding.FragmentHomeBinding
import com.stadio.textbookstore.viewmodel.BookListViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val bookViewModel: BookListViewModel by activityViewModels()
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeBooks()
        setupSearch()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter { book ->
            bookViewModel.selectBook(book.id)
            findNavController().navigate(R.id.action_home_to_details)
        }

        binding.bookList.layoutManager = LinearLayoutManager(requireContext())
        binding.bookList.adapter = bookAdapter
    }

    private fun observeBooks() {
        bookViewModel.books.observe(viewLifecycleOwner) { books ->
            bookAdapter.submitList(books)
            binding.emptyText.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupSearch() {
        binding.searchInput.addTextChangedListener { editable ->
            bookViewModel.search(editable?.toString().orEmpty())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}