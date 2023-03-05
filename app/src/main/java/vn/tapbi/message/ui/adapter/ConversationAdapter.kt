package vn.tapbi.message.ui.adapter

import android.os.Build
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import vn.tapbi.message.R
import vn.tapbi.message.data.model.Conversation
import vn.tapbi.message.databinding.ItemSeenMessageBinding
import vn.tapbi.message.databinding.ItemUnseenMessageBinding
import vn.tapbi.message.utils.formatDate
import java.util.*
import java.text.SimpleDateFormat

private val conversationDiff = object : DiffUtil.ItemCallback<Conversation>() {
    override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
        return oldItem.address == newItem.address
    }

    override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
        return oldItem == newItem
    }
}

@RequiresApi(Build.VERSION_CODES.N)
class ConversationAdapter :
    ListAdapter<Conversation, RecyclerView.ViewHolder>(conversationDiff) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).read == 1)
            1
        else
            2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1)
            ViewHolder1(
                ItemSeenMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        else
            ViewHolder2(
                ItemUnseenMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder1)
            holder.bind(getItem(position))
        else
            (holder as ViewHolder2).bind(getItem(position))
    }


    inner class ViewHolder1(private val binding: ItemSeenMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(conversation: Conversation) {
            bindConversation(
                viewBinding = binding,
                tvTime = binding.tvTime,
                tvUserName = binding.tvUserName,
                tvMessageContent = binding.tvMessageContent,
                ivAvatar = binding.ivAvatar,
                conversation = conversation
            )
        }
    }

    inner class ViewHolder2(private val binding: ItemUnseenMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(conversation: Conversation) {
            bindConversation(
                viewBinding = binding,
                tvTime = binding.tvTime,
                tvUserName = binding.tvUserName,
                tvMessageContent = binding.tvMessageContent,
                ivAvatar = binding.ivAvatar,
                conversation = conversation
            )
        }
    }

    private fun bindConversation(
        viewBinding: ViewBinding,
        tvTime: TextView,
        tvUserName: TextView,
        tvMessageContent: TextView,
        ivAvatar: ImageView,
        conversation: Conversation
    ) {
        tvTime.text = formatDate(conversation.date)
        tvUserName.text = conversation.contactName
        tvMessageContent.text = conversation.snippet
        Glide.with(ivAvatar).load(conversation.photoLink)
            .placeholder(R.drawable.default_avatar_icon).circleCrop()
            .into(ivAvatar)
        viewBinding.root.setOnClickListener {
            onItemClickListener?.let {
                it(conversation)
            }

        }
    }

    private var onItemClickListener: ((Conversation) -> Unit)? = null

    fun setOnItemClickListener(listener: (Conversation) -> Unit) {
        onItemClickListener = listener
    }
}