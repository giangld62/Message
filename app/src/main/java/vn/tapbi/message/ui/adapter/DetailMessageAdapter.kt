package vn.tapbi.message.ui.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.tapbi.message.data.model.Message
import vn.tapbi.message.databinding.ItemErrorMessageBinding
import vn.tapbi.message.databinding.ItemIncomingMessageBinding
import vn.tapbi.message.databinding.ItemOutgoingMessageBinding

private val detailMessageDiff = object : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}

class DetailMessageAdapter(private val context: Context) :
    ListAdapter<Message, RecyclerView.ViewHolder>(detailMessageDiff) {
    private var fontLink: String? = null

    fun setFont(fontLink: String) {
        this.fontLink = fontLink
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            1 -> 1
            2 -> 2
            else -> 3
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> IncomingMessageViewHolder(
                ItemIncomingMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            2 -> OutgoingMessageViewHolder(
                ItemOutgoingMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> ErrorMessageViewHolder(
                ItemErrorMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is IncomingMessageViewHolder -> holder.bind(getItem(position))
            is OutgoingMessageViewHolder -> holder.bind(getItem(position))
            is ErrorMessageViewHolder -> holder.bind(getItem(position))
        }
    }

    inner class IncomingMessageViewHolder(private val binding: ItemIncomingMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            bindMessage(message, binding.tvIncomingMessage)
        }
    }

    inner class OutgoingMessageViewHolder(private val binding: ItemOutgoingMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            bindMessage(message, binding.tvOutgoingMessage)
        }
    }

    inner class ErrorMessageViewHolder(private val binding: ItemErrorMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            bindMessage(message, binding.tvOutgoingMessage)
            binding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(message)
                }
            }
        }
    }

    private fun bindMessage(
        message: Message, textView: TextView
    ) {
        fontLink?.let {
            textView.typeface =
                Typeface.createFromAsset(context.assets, fontLink)
        }
        textView.text = message.body
    }

    private var onItemClickListener: ((Message) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Message) -> Unit)){
        onItemClickListener = listener
    }
}