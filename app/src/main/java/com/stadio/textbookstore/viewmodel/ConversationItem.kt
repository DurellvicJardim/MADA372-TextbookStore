package com.stadio.textbookstore.viewmodel

import com.stadio.textbookstore.data.Message
import com.stadio.textbookstore.data.User

data class ConversationItem(
    val otherUser: User,
    val lastMessage: Message
)