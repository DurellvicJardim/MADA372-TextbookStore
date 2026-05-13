package com.stadio.textbookstore.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stadio.textbookstore.R
import com.stadio.textbookstore.data.Book
import com.stadio.textbookstore.databinding.BookCardBinding

class BookAdapter(
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private var books: List<Book> = emptyList()

    fun submitList(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = BookCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    inner class BookViewHolder(
        private val binding: BookCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            val context = binding.root.context
            binding.titleText.text = book.title
            binding.authorText.text = book.author
            binding.priceText.text = context.getString(R.string.price_format, book.price)
            binding.conditionText.text = context.getString(R.string.condition_label, book.condition)

            binding.root.setOnClickListener {
                onBookClick(book)
            }
        }
    }
}