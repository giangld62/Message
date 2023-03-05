package vn.tapbi.message.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.tapbi.message.R
import vn.tapbi.message.common.Constant
import vn.tapbi.message.databinding.ItemStarBinding

class RateAppAdapter(private val stars: List<Int>) :
    RecyclerView.Adapter<RateAppAdapter.ViewHolder>() {

    private var checkedPosition = Constant.DEFAULT_STAR_POSITION

    fun setCheckedStar(star: Int){
        if(star != Constant.DEFAULT_STAR_POSITION){
            checkedPosition = star - 1
            notifyItemChanged(checkedPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stars[position])
    }

    override fun getItemCount(): Int {
        return stars.size
    }

    inner class ViewHolder(private val binding: ItemStarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(star: Int) {
            if (adapterPosition <= checkedPosition) {
                binding.ivStar.setImageResource(R.drawable.star_1)
            } else {
                binding.ivStar.setImageResource(star)
            }
            binding.root.setOnClickListener {
                checkedPosition = adapterPosition
                onItemClickListener?.let {
                    it(adapterPosition+1)
                }
                notifyItemRangeChanged(0,5)
            }
        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null
    fun setOnItemClickListener(listener: ((Int) -> Unit)) {
        onItemClickListener = listener
    }
}