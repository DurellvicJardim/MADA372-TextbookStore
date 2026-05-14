package com.stadio.textbookstore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stadio.textbookstore.data.BookStoreRepository
import com.stadio.textbookstore.data.Message

class MessagesViewModel : ViewModel() {

    private val _conversations = MutableLiveData<List<ConversationItem>>()
    val conversations: LiveData<List<ConversationItem>> = _conversations

    private val _activeThread = MutableLiveData<List<Message>>()
    val activeThread: LiveData<List<Message>> = _activeThread
    private val _activeOtherUser = MutableLiveData<com.stadio.textbookstore.data.User?>()
    val activeOtherUser: LiveData<com.stadio.textbookstore.data.User?> = _activeOtherUser

    private var activeOtherUserId: String? = null
    private var lastLoadedUserId: String? = null

    //load conversation list for user
    fun loadConversations(userId: String) {
        lastLoadedUserId = userId
        val messages = BookStoreRepository.getConversationsFor(userId)
        val items = messages.mapNotNull { msg ->
            val otherId = if (msg.senderId == userId) msg.receiverId else msg.senderId
            val otherUser = BookStoreRepository.getUserById(otherId) ?: return@mapNotNull null
            ConversationItem(otherUser, msg)
        }
        _conversations.value = items
    }

    //Open message thread between current and other user
    fun openThread(currentUserId: String, otherUserId: String) {
        activeOtherUserId = otherUserId
        _activeOtherUser.value = BookStoreRepository.getUserById(otherUserId)
        _activeThread.value = BookStoreRepository.getMessagesBetween(currentUserId, otherUserId)
    }

    //Send a message in currently active thread
    fun sendMessage(senderId: String, content: String, bookId: String? = null) {
        val receiverId = activeOtherUserId ?: return
        BookStoreRepository.addMessage(senderId, receiverId, content, bookId)
        _activeThread.value = BookStoreRepository.getMessagesBetween(senderId, receiverId)
        // Also refresh the conversations list so it updates when the user backs out
        lastLoadedUserId?.let { loadConversations(it) }
    }

    fun closeThread() {
        activeOtherUserId = null
        _activeOtherUser.value = null
        _activeThread.value = emptyList()
    }
}