package com.stadio.textbookstore.data

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val isbn: String,
    val price: Double,
    val condition: String,
    val description: String,
    val sellerId: String,
    val coverResId: Int? = null
)