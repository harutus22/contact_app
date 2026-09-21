package com.example.contactapp.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.data_models.Contact
import com.example.contactapp.databinding.ContactItemBinding
import androidx.core.net.toUri

class ContactRecyclerViewAdapter(private val onItemClicked: (Contact) -> Unit) :
    ListAdapter<Contact, ContactRecyclerViewAdapter.ContactHolder>(StringDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactHolder {
        val binding = ContactItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ContactHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ContactHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class ContactHolder(private val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.fullName.text = contact.name
            binding.number.text = contact.mobilePhones[0]
            if (contact.photoUri != null)
                binding.image.setImageURI(contact.photoUri.toUri())
            binding.root.setOnClickListener {
                onItemClicked(contact)
            }
        }
    }

    class StringDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(
            oldItem: Contact,
            newItem: Contact
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Contact,
            newItem: Contact
        ): Boolean {
            return oldItem == newItem
        }
    }
}