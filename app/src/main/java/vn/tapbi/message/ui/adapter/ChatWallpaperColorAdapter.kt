package vn.tapbi.message.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import vn.tapbi.message.R
import vn.tapbi.message.common.Constant.DEFAULT_WALLPAPER_COLOR_VALUE
import vn.tapbi.message.common.Constant.DEFAULT_WALLPAPER_LANDSCAPE_VALUE
import vn.tapbi.message.common.Constant.UNCHECK_POSITION
import vn.tapbi.message.databinding.ItemChatWallpaperColorBinding

class ChatWallpaperColorAdapter(private val colors: List<Int>, private val context: Context) :
    RecyclerView.Adapter<ChatWallpaperColorAdapter.ViewHolder>() {
    private var checkedPosition = UNCHECK_POSITION

    fun unCheck() {
        val notifyItemPosition = checkedPosition
        checkedPosition = UNCHECK_POSITION
        notifyItemChanged(notifyItemPosition)
    }

    fun setCheckedColor(color: Int) {
        for (i in colors.indices) {
            if (colors[i] == color) {
                checkedPosition = i
                break
            }
        }
        notifyItemChanged(checkedPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatWallpaperColorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(colors[position])
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    inner class ViewHolder(private val binding: ItemChatWallpaperColorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(color: Int) {
            binding.sivColor.setImageResource(color)
            if (checkedPosition == adapterPosition) {
                binding.flRoot.background = ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.bg_stroke_blue_corners_12, null
                )
                binding.flRoot.setPadding(8)
            } else {
                binding.flRoot.background = ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.bg_stroke_gray_corners_12, null
                )
                binding.flRoot.setPadding(1)
            }
            binding.root.setOnClickListener {
                if (checkedPosition != adapterPosition) {
                    binding.flRoot.background = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.bg_stroke_blue_corners_12, null
                    )
                    binding.flRoot.setPadding(8)
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                    onItemClickListener?.let {
                        it(color)
                    }
                }
            }
        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }
}