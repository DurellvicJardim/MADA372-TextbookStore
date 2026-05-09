package com.stadio.textbookstore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stadio.textbookstore.data.BookStoreRepository
import com.stadio.textbookstore.data.Message

//Manages conversation list and active conversation thread
class MessagesViewModel : ViewModel() {

    private val _conversations = MutableLiveData<List<Message>>()
    val conversations: LiveData<List<Message>> = _conversations

    private val _activeThread = MutableLiveData<List<Message>>()
    val activeThread: LiveData<List<Message>> = _activeThread

    private var activeOtherUserId: String? = null

    //load latest message
    fun loadConversations(userId: String) {
        _conversations.value = BookStoreRepository.getConversationsFor(userId)
    }

    //open message thread between current user and another user
    fun openThread(currentUserId: String, otherUserId: String) {
        activeOtherUserId = otherUserId
        _activeThread.value = BookStoreRepository.getMessagesBetween(currentUserId, otherUserId)
    }

    //Send a message in currently active thread
    fun sendMessage(senderId: String, content: String, bookId: String? = null) {
        val receiverId = activeOtherUserId ?: return
        BookStoreRepository.addMessage(senderId, receiverId, content, bookId)
        _activeThread.value = BookStoreRepository.getMessagesBetween(senderId, receiverId)
        _conversations.value = BookStoreRepository.getConversationsFor(senderId)
    }

    fun closeThread() {
        activeOtherUserId = null
        _activeThread.value = emptyList()
    }
}