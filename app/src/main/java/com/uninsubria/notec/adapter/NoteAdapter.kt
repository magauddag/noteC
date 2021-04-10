package com.uninsubria.notec.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uninsubria.notec.R
import com.uninsubria.notec.data.Note
import kotlinx.android.synthetic.main.card_note.view.*
import kotlinx.android.synthetic.main.card_note_no_image.view.*

class NoteAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    private val layout_image = R.layout.card_note
    private val layout_no_image = R.layout.card_note_no_image
    private var notes = emptyList<Note>()
    var onItemClick: ((Note) -> Unit)? = null
    var onItemLongClick: ((Note) -> Unit)? = null

    inner class NoteViewHolder (itemView : View): RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.tv_title
        val body: TextView = itemView.tv_body
        val image: ImageView = itemView.imageView
        val date: TextView = itemView.tv_data
        val category: TextView = itemView.tv_category

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(notes[adapterPosition])
            }

            itemView.setOnLongClickListener {
                onItemLongClick?.invoke(notes[adapterPosition])
                return@setOnLongClickListener true
            }
        }
    }

    inner class NoteViewHolder2 (itemView : View): RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.tv_title2
        val body: TextView = itemView.tv_body2
        val date: TextView = itemView.tv_data2
        val category: TextView = itemView.tv_category2

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(notes[adapterPosition])
            }

            itemView.setOnLongClickListener {
                onItemLongClick?.invoke(notes[adapterPosition])
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (notes[position].image == 0)
            layout_no_image
        else
            layout_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == layout_image) {
            val itemView = LayoutInflater.from(parent.context).inflate(layout_image, parent, false)
            return NoteViewHolder(itemView)
        }
        else {
            val itemView = LayoutInflater.from(parent.context).inflate(layout_no_image, parent, false)
            return NoteViewHolder2(itemView)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = notes[position]

        if (holder.itemViewType == layout_image) {
            val itemHolder = holder as NoteViewHolder
            itemHolder.title.text = currentItem.title
            itemHolder.body.text = currentItem.body
            itemHolder.image.setImageResource(currentItem.image)
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

    fun setData (notes : List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }
}