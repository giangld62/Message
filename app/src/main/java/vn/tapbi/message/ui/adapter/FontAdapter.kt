package vn.tapbi.message.ui.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import vn.tapbi.message.common.Constant.UNCHECK_POSITION
import vn.tapbi.message.data.model.Font
import vn.tapbi.message.databinding.ItemFontBinding

private val fontDiff = object : DiffUtil.ItemCallback<Font>(){
    override fun areItemsTheSame(oldItem: Font, newItem: Font): Boolean {
        return oldItem.fontName == newItem.fontName
    }

    override fun areContentsTheSame(oldItem: Font, newItem: Font): Boolean {
        return oldItem == newItem
    }
}

class FontAdapter :
    ListAdapter<Font,FontAdapter.ViewHolder>(fontDiff) {
    private var checkedPosition = UNCHECK_POSITION

    fun setCheckedFont(fontResource: String){
        for(i in currentList.indices){
            if(getItem(i).fontLink == fontResource){
                checkedPosition = i
                break
            }
        }
        notifyItemChanged(checkedPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFontBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(private val binding: ItemFontBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(font: Font) {
            binding.tvFont.text = font.fontName
            binding.tvFont.typeface = Typeface.createFromAsset(binding.root.context.assets,font.fontLink)
            if(checkedPosition == adapterPosition){
                binding.ivSelected.visibility = View.VISIBLE
            }
            else{
                binding.ivSelected.visibility = View.GONE
            }
            binding.root.setOnClickListener {
                if(checkedPosition != adapterPosition){
                    binding.ivSelected.visibility = View.VISIBLE
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                    onItemClickListener?.let {
                        it(font.fontLink)
                    }
                }
            }
        }
    }

    private var onItemClickListener: ((String) -> Unit)? = null
    fun setOnItemClickListener(listener: ((String) -> Unit)){
        onItemClickListener = listener
    }
}