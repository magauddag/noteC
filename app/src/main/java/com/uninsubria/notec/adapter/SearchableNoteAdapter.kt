package com.uninsubria.notec.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uninsubria.notec.R
import com.uninsubria.notec.database.model.Note
import kotlinx.android.synthetic.main.card_note.view.imageView
import kotlinx.android.synthetic.main.card_note.view.tv_body
import kotlinx.android.synthetic.main.card_note.view.tv_category
import kotlinx.android.synthetic.main.card_note.view.tv_data
import kotlinx.android.synthetic.main.card_note.view.tv_title
import kotlinx.android.synthetic.main.card_note_no_image_material.view.*

class SearchableNoteAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val layoutImage = R.layout.card_note_material
    private val layoutNoImage = R.layout.card_note_no_image_material

    var notes = emptyList<Note>()
    var onItemClick: ((Note) -> Unit)? = null

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.tv_title
        val body: TextView = itemView.tv_body
        val image: ImageView = itemView.imageView
        val date: TextView = itemView.tv_data
        val category: TextView = itemView.tv_category

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(notes[bindingAdapterPosition])
            }
        }
    }

    inner class NoteViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.tv_title3
        val body: TextView = itemView.tv_body3
        val date: TextView = itemView.tv_data3
        val category: TextView = itemView.tv_category3

        init {

            itemView.setOnClickListener {
                onItemClick?.invoke(notes[bindingAdapterPosition])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (notes[position].image == null)
            layoutNoImage
        else
            layoutImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == layoutImage) {
            val itemView = LayoutInflater.from(parent.context).inflate(layoutImage, parent, false)
            return NoteViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(layoutNoImage, parent, false)
            return NoteViewHolder2(itemView)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = notes[position]

        if (holder.itemViewType == layoutImage) {
            val itemHolder = holder as NoteViewHolder

            itemHolder.title.text = currentItem.title
            itemHolder.body.text = currentItem.body
            itemHolder.image.setImageURI(Uri.parse(currentItem.image))
            itemHolder.date.text = currentItem.data
            itemHolder.category.text = currentItem.category


        } else {
            val itemHolder = holder as NoteViewHolder2
            itemHolder.title.text = currentItem.title
            itemHolder.body.text = currentItem.body
            itemHolder.date.text = currentItem.data
            itemHolder.category.text = currentItem.category

        }
    }

    fun setData(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

}