package com.stadio.textbookstore.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stadio.textbookstore.databinding.ConversationItemBinding
import com.stadio.textbookstore.util.formatTimestamp
import com.stadio.textbookstore.viewmodel.ConversationItem

class ConversationAdapter(
    private val onConversationClick: (ConversationItem) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    private var conversations: List<ConversationItem> = emptyList()

    fun submitList(newConversations: List<ConversationItem>) {
        conversations = newConversations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ConversationItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount(): Int = conversations.size

    inner class ConversationViewHolder(
        private val binding: ConversationItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ConversationItem) {
            binding.userNameText.text = item.otherUser.fullName
            binding.lastMessageText.text = item.lastMessage.content
            binding.timestampText.text = formatTimestamp(item.lastMessage.timestamp)

            if (item.otherUser.profilePicUri != null) {
                binding.conversationAvatar.setImageURI(android.net.Uri.parse(item.otherUser.profilePicUri))
            } else {
                binding.conversationAvatar.setImageURI(null)
            }

            binding.root.setOnClickListener {
                onConversationClick(item)
            }
        }
    }
}