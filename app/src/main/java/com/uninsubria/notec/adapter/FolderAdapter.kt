package com.uninsubria.notec.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.uninsubria.notec.R
import com.uninsubria.notec.database.model.Folder
import kotlinx.android.synthetic.main.card_folder.view.*

class FolderAdapter : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    companion object {
        var selectedCount = 0

        fun getCount(): Int {
            return selectedCount
        }

        fun setCount(num: Int) {
            selectedCount = num
        }
    }

    var folders = emptyList<Folder>()
    var onItemClick: ((Folder) -> Unit)? = null
    var onItemSelected: ((Folder, Boolean, Int) -> Unit)? = null
    var onItemLongClick: ((Folder, Boolean, Int) -> Unit)? = null

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var category: TextView = itemView.tv_folder_category
        var card: MaterialCardView = itemView.folder_card

        init {

            itemView.setOnClickListener {

                if (card.isChecked && category.text != "Default") {
                    card.isChecked = false
                    onItemSelected?.invoke(folders[bindingAdapterPosition], card.isChecked, bindingAdapterPosition)
                } else if (selectedCount > 0 && !card.isChecked && category.text != "Default") {
                    card.isChecked = true
                    onItemSelected?.invoke(folders[bindingAdapterPosition], card.isChecked, bindingAdapterPosition)
                } else if (selectedCount == 0)
                    onItemClick?.invoke(folders[bindingAdapterPosition])
            }

            itemView.setOnLongClickListener {
                if (selectedCount == 0 && category.text != "Default") {
                    card.isChecked = !card.isChecked
                    onItemLongClick?.invoke(folders[bindingAdapterPosition], card.isChecked, bindingAdapterPosition)
                }

                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_folder, parent, false)
        return FolderViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val currentItem = folders[position]
        holder.category.text = currentItem.category

        holder.card.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                selectedCount++
            else
                selectedCount--
        }

    }

    fun setData(folders: List<Folder>) {
        this.folders = folders
        notifyDataSetChanged()
    }
}