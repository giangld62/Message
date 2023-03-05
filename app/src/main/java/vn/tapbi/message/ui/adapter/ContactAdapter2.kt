package vn.tapbi.message.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.tapbi.message.R
import vn.tapbi.message.data.model.Contact
import vn.tapbi.message.databinding.ItemContact2Binding

private val contact2Diff = object: DiffUtil.ItemCallback<Contact>(){
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.contactId == newItem.contactId
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}

class ContactAdapter2 : ListAdapter<Contact,ContactAdapter2.ViewHolder>(contact2Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemContact2Binding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemContact2Binding) : RecyclerView.ViewHolder(binding.root){
        fun bind(contact: Contact){
            binding.tvUserName.text = contact.name.ifEmpty { contact.phoneNumber }
            if(contact.photoUri == null){
                binding.ivAvatar.setImageResource(R.drawable.default_avatar_icon)
            }
            else{
                Glide.with(binding.ivAvatar).load(contact.photoUri).placeholder(R.drawable.default_avatar_icon).into(binding.ivAvatar)
            }
            binding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(contact.contactId)
                }
            }
        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit){
        onItemClickListener = listener
    }
}