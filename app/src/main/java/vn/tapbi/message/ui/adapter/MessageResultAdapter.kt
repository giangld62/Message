package vn.tapbi.message.ui.adapter

import android.annotation.SuppressLint
import android.content.res.Resources
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.tapbi.message.data.model.Message
import vn.tapbi.message.databinding.ItemMessageBinding
import vn.tapbi.message.utils.formatDate
import java.text.SimpleDateFormat
import java.util.*

class MessageResultAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<MessageResultAdapter.ViewHolder>() {

    private var isShowMore = false
    fun setShowMore(showMore: Boolean) {
        isShowMore = showMore
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int {
        if (!isShowMore) {
            if (messages.size > 5)
                return 5
        }
        return messages.size
    }

    class ViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(message: Message) {
            binding.tvTime.text = formatDate(message.date)
            binding.tvMessageContent.text = message.body
        }
    }
}