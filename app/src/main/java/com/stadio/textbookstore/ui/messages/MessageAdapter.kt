package com.stadio.textbookstore.ui.messages

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.stadio.textbookstore.R
import com.stadio.textbookstore.data.Message
import com.stadio.textbookstore.databinding.MessageItemBinding
import com.stadio.textbookstore.util.formatMessageTime

class MessageAdapter(
    private val currentUserId: String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var messages: List<Message> = emptyList()

    fun submitList(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = MessageItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    inner class MessageViewHolder(
        private val binding: MessageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            val context = binding.root.context
            val isMine = message.senderId == currentUserId

            binding.messageText.text = message.content
            binding.timestampText.text = formatMessageTime(message.timestamp)

            //style bubble based on who sent it
            val params = binding.bubble.layoutParams as FrameLayout.LayoutParams
            if (isMine) {
                params.gravity = Gravity.END
                binding.bubble.setBackgroundResource(R.drawable.bubble_me)
                binding.messageText.setTextColor(ContextCompat.getColor(context, R.color.off_white))
                binding.timestampText.setTextColor(ContextCompat.getColor(context, R.color.off_white))
            } else {
                params.gravity = Gravity.START
                binding.bubble.setBackgroundResource(R.drawable.bubble_them)
                binding.messageText.setTextColor(ContextCompat.getColor(context, R.color.charcoal))
                binding.timestampText.setTextColor(ContextCompat.getColor(context, R.color.charcoal_muted))
            }
            binding.bubble.layoutParams = params
        }
    }
}