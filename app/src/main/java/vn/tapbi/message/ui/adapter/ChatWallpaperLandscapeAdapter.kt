package vn.tapbi.message.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.tapbi.message.common.Constant.DEFAULT_WALLPAPER_LANDSCAPE_VALUE
import vn.tapbi.message.common.Constant.UNCHECK_POSITION
import vn.tapbi.message.databinding.ItemChatWallpaperLandscapeBinding

class ChatWallpaperLandscapeAdapter :
    RecyclerView.Adapter<ChatWallpaperLandscapeAdapter.ViewHolder>() {
    private var checkedPosition = UNCHECK_POSITION

    private val differCallBack = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.length == newItem.length
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.contentEquals(newItem)
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    fun unCheck() {
        val notifyItemPosition = checkedPosition
        checkedPosition = UNCHECK_POSITION
        notifyItemChanged(notifyItemPosition)
    }

    fun setCheckedLandscape(landscapeSelected: String?) {
        for(i in differ.currentList.indices){
            if(differ.currentList[i] == landscapeSelected){
                checkedPosition = i
                break
            }
        }
        notifyItemChanged(checkedPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemChatWallpaperLandscapeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(val binding: ItemChatWallpaperLandscapeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(link: String) {
            Glide.with(binding.ivLandscape).load(link).into(binding.ivLandscape)
            if (checkedPosition == adapterPosition) {
                binding.ivChecked.visibility = View.VISIBLE
            } else {
                binding.ivChecked.visibility = View.GONE
            }
            binding.root.setOnClickListener {
                if (checkedPosition != adapterPosition) {
                    binding.ivChecked.visibility = View.VISIBLE
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                }
                onItemClickListener?.let {
                    it(link)
                }
            }
        }
    }

    private var onItemClickListener: ((String) -> Unit)? = null
    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }
}