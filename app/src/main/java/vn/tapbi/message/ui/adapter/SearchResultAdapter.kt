package vn.tapbi.message.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import vn.tapbi.message.data.model.SearchResult
import vn.tapbi.message.databinding.ItemAddressSearchBinding
import vn.tapbi.message.databinding.ItemMessageBodySearchBinding
import vn.tapbi.message.utils.gone
import vn.tapbi.message.utils.show

private val searchDiff = object : DiffUtil.ItemCallback<SearchResult>() {
    override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
        return oldItem.threadId == newItem.threadId
    }

    override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
        return oldItem == newItem
    }
}

class SearchResultAdapter(private val inter: OnItemClick) : ListAdapter<SearchResult, RecyclerView.ViewHolder>(searchDiff) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).messages.isEmpty())
            1
        else
            2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> ViewHolder1(
                ItemAddressSearchBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> ViewHolder2(
                ItemMessageBodySearchBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder1)
            holder.bind(getItem(position))
        else
            (holder as ViewHolder2).bind(getItem(position))
    }

    inner class ViewHolder1(private val binding: ItemAddressSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(searchResult: SearchResult) {
            binding.tvContactName.text = searchResult.contactName
            if (searchResult.messageCount > 1) {
                binding.tvMessagesCount.text = "${searchResult.messageCount} messages"
            } else {
                binding.tvMessagesCount.text = "${searchResult.messageCount} message"
            }
            binding.root.setOnClickListener {
                inter.onItemClick(searchResult)
            }
        }
    }

    inner class ViewHolder2(private val binding: ItemMessageBodySearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(searchResult: SearchResult) {
            binding.tvContactName.text = searchResult.contactName
            if(searchResult.messages.size > 5) binding.tvShowMore.show() else binding.tvShowMore.gone()
            binding.rvMessages.setHasFixedSize(true)
            binding.rvMessages.layoutManager = LinearLayoutManager(binding.root.context)
            val adapter = MessageResultAdapter(searchResult.messages)
            binding.rvMessages.adapter = adapter
            binding.tvContactName.setOnClickListener {
                inter.onItemClick(searchResult)
            }
            binding.viewRv.setOnClickListener {
                Timber.d("ItemMessageBodySearchBinding ${searchResult.threadId}")
                inter.onItemClick(searchResult)
            }
            binding.tvShowMore.setOnClickListener {
                adapter.setShowMore(true)
                adapter.notifyDataSetChanged()
                binding.tvShowMore.gone()
            }
        }
    }

    interface OnItemClick{
        fun onItemClick(searchResult: SearchResult)
    }
}