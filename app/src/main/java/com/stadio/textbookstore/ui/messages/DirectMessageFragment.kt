package com.stadio.textbookstore.ui.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stadio.textbookstore.databinding.FragmentDirectMessageBinding
import com.stadio.textbookstore.viewmodel.MessagesViewModel
import com.stadio.textbookstore.viewmodel.UserViewModel

class DirectMessageFragment : Fragment() {

    private var _binding: FragmentDirectMessageBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private val messagesViewModel: MessagesViewModel by activityViewModels()
    private lateinit var adapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDirectMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserId = userViewModel.currentUser.value?.id
        if (currentUserId == null) {
            findNavController().popBackStack()
            return
        }

        setupRecyclerView(currentUserId)
        observeThread()
        observeOtherUser()

        binding.topBar.setNavigationOnClickListener {
            messagesViewModel.closeThread()
            findNavController().popBackStack()
        }

        binding.sendButton.setOnClickListener {
            sendCurrentMessage(currentUserId)
        }
    }

    private fun setupRecyclerView(currentUserId: String) {
        adapter = MessageAdapter(currentUserId)
        binding.messagesList.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true   //newest messages at the bottom
        }
        binding.messagesList.adapter = adapter
    }

    private fun observeThread() {
        messagesViewModel.activeThread.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages)
            binding.emptyText.visibility = if (messages.isEmpty()) View.VISIBLE else View.GONE
            //Scroll to bottom on new content
            if (messages.isNotEmpty()) {
                binding.messagesList.scrollToPosition(messages.size - 1)
            }
        }
    }

    private fun observeOtherUser() {
        messagesViewModel.activeOtherUser.observe(viewLifecycleOwner) { user ->
            binding.topBar.title = user?.fullName ?: "Conversation"
        }
    }

    private fun sendCurrentMessage(currentUserId: String) {
        val content = binding.messageInput.text?.toString()?.trim().orEmpty()
        if (content.isEmpty()) return
        messagesViewModel.sendMessage(currentUserId, content)
        binding.messageInput.text = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}