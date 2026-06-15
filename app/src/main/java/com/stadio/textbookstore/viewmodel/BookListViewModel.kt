package com.stadio.textbookstore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stadio.textbookstore.data.Book
import com.stadio.textbookstore.data.BookStoreRepository
import androidx.lifecycle.map
import com.stadio.textbookstore.data.User

//Manages list of books on home screen, search query, and currently selected book.
class BookListViewModel : ViewModel() {

    private val _books = MutableLiveData<List<Book>>(BookStoreRepository.getAllBooks())
    val books: LiveData<List<Book>> = _books

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    //condition filter
    private var conditionFilter: String? = null

    //sort the list by price from lowest to highest
    private var sortByPriceAsc: Boolean = false

    private val _selectedBook = MutableLiveData<Book?>()
    val selectedBook: LiveData<Book?> = _selectedBook

    //whenever selectedBook changes look up seller
    val selectedSeller: LiveData<User?> = _selectedBook.map { book ->
        book?.let { BookStoreRepository.getUserById(it.sellerId) }
    }

    fun search(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun refresh() {
        applyFilters()
    }

    //when the user taps a condition chip
    fun setConditionFilter(condition: String?) {
        conditionFilter = condition
        applyFilters()
    }

    //when user toggles price sort chip
    fun setPriceSort(ascending: Boolean) {
        sortByPriceAsc = ascending
        applyFilters()
    }

    //search text, condition filter and price sort combined into one function
    private fun applyFilters() {
        var result = BookStoreRepository.searchBooks(_searchQuery.value ?: "")
        val condition = conditionFilter
        if (condition != null) {
            result = result.filter { it.condition.equals(condition, ignoreCase = true) }
        }
        if (sortByPriceAsc) {
            result = result.sortedBy { it.price }
        }
        _books.value = result
    }

    fun selectBook(bookId: String) {
        _selectedBook.value = BookStoreRepository.getBookById(bookId)
    }

    fun clearSelection() {
        _selectedBook.value = null
    }

    fun addBook(
        title: String, author: String, isbn: String, price: Double,
        condition: String, description: String, sellerId: String,
        coverUri: String? = null
    ): Book {
        val book = BookStoreRepository.addBook(
            title, author, isbn, price, condition, description, sellerId, coverUri
        )
        refresh()
        return book
    }
}