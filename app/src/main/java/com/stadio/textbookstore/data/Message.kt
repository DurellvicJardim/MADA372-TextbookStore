package com.stadio.textbookstore.data

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Long,
    val bookId: String? = null
)