package com.uninsubria.notec.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uninsubria.notec.R
import com.uninsubria.notec.data.Folder
import kotlinx.android.synthetic.main.card_folder.view.*

class FolderAdapter(var folders: ArrayList<Folder>): RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    inner class FolderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var category: TextView = itemView.tv_folder_category
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
    }
}