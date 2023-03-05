package vn.tapbi.message.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.tapbi.message.data.model.Contact
import vn.tapbi.message.databinding.ItemContactBinding

private val contactDiff = object: DiffUtil.ItemCallback<Contact>(){
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.contactId == newItem.contactId
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}

class ContactAdapter : ListAdapter<Contact,ContactAdapter.ViewHolder>(contactDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemContactBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(contact: Contact){
            binding.tvUserName.text = contact.name
            binding.tvPhoneNumber.text = contact.phoneNumber
            binding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(contact)
                }
            }
        }
    }

    private var onItemClickListener: ((Contact) -> Unit)? = null

    fun setOnItemClickListener(listener: (Contact) -> Unit){
        onItemClickListener = listener
    }
}