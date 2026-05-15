package com.stadio.textbookstore.ui.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stadio.textbookstore.R
import com.stadio.textbookstore.databinding.FragmentMessagesBinding
import com.stadio.textbookstore.viewmodel.MessagesViewModel
import com.stadio.textbookstore.viewmodel.UserViewModel

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private val messagesViewModel: MessagesViewModel by activityViewModels()
    private lateinit var adapter: ConversationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeConversations()
    }

    override fun onResume() {
        super.onResume()
        // Refresh every time screen comes back so new  messages appear immediately
        val currentUser = userViewModel.currentUser.value
        if (currentUser != null) {
            messagesViewModel.loadConversations(currentUser.id)
        }
    }

    private fun setupRecyclerView() {
        adapter = ConversationAdapter { item ->
            val currentUser = userViewModel.currentUser.value ?: return@ConversationAdapter
            messagesViewModel.openThread(currentUser.id, item.otherUser.id)
            findNavController().navigate(R.id.action_messages_to_thread)
        }
        binding.conversationList.layoutManager = LinearLayoutManager(requireContext())
        binding.conversationList.adapter = adapter
    }

    private fun observeConversations() {
        messagesViewModel.conversations.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.emptyText.visibility =
                if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}