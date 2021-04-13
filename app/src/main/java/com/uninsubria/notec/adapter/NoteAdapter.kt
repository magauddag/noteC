package com.uninsubria.notec.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.uninsubria.notec.R
import com.uninsubria.notec.data.Note
import kotlinx.android.synthetic.main.card_note.view.imageView
import kotlinx.android.synthetic.main.card_note.view.tv_body
import kotlinx.android.synthetic.main.card_note.view.tv_category
import kotlinx.android.synthetic.main.card_note.view.tv_data
import kotlinx.android.synthetic.main.card_note.view.tv_title
import kotlinx.android.synthetic.main.card_note_material.view.*
import kotlinx.android.synthetic.main.card_note_no_image_material.view.*

class NoteAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        var selectedCount = 0

        fun getCount(): Int {
            return selectedCount
        }

        fun setCount(num: Int) {
            selectedCount = num
        }
    }

    private val layoutImage = R.layout.card_note_material
    private val layoutNoImage = R.layout.card_note_no_image_material

    private var notes = emptyList<Note>()
    var onItemClick: ((Note) -> Unit)? = null
    var onItemLongClick: ((Note) -> Unit)? = null
    var onItemSelected: ((Note) -> Unit)? = null

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.tv_title
        val body: TextView = itemView.tv_body
        val image: ImageView = itemView.imageView
        val date: TextView = itemView.tv_data
        val category: TextView = itemView.tv_category
        val card : MaterialCardView = itemView.card_note_material_image

        init {

            itemView.setOnClickListener {

                if (card.isChecked) {
                    card.isChecked = false
                    onItemSelected?.invoke(notes[adapterPosition])
                } else if (selectedCount > 0 && !card.isChecked) {
                    card.isChecked = true
                    onItemSelected?.invoke(notes[adapterPosition])
                } else
                    onItemClick?.invoke(notes[adapterPosition])
            }

            itemView.setOnLongClickListener {
                card.isChecked = !card.isChecked
                onItemLongClick?.invoke(notes[adapterPosition])
                return@setOnLongClickListener true
            }
        }
    }

    inner class NoteViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.tv_title3
        val body: TextView = itemView.tv_body3
        val date: TextView = itemView.tv_data3
        val category: TextView = itemView.tv_category3
        val card: MaterialCardView = itemView.card_note_material

        init {

            itemView.setOnClickListener {

                if (card.isChecked) {
                    card.isChecked = false
                    onItemSelected?.invoke(notes[adapterPosition])
                } else if (selectedCount > 0 && !card.isChecked) {
                    card.isChecked = true
                    onItemSelected?.invoke(notes[adapterPosition])
                } else
                    onItemClick?.invoke(notes[adapterPosition])
            }

            itemView.setOnLongClickListener {
                card.isChecked = !card.isChecked
                onItemLongClick?.invoke(notes[adapterPosition])
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (notes[position].image == 0)
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
            itemHolder.image.setImageResource(currentItem.image)
            itemHolder.date.text = currentItem.data
            itemHolder.category.text = currentItem.category

            itemHolder.card.setOnCheckedChangeListener { _, isChecked ->
                currentItem.selected = isChecked

                if (isChecked)
                    selectedCount++
                else
                    selectedCount--
            }

        } else {
            val itemHolder = holder as NoteViewHolder2
            itemHolder.title.text = currentItem.title
            itemHolder.body.text = currentItem.body
            itemHolder.date.text = currentItem.data
            itemHolder.category.text = currentItem.category

            itemHolder.card.setOnCheckedChangeListener { _, isChecked ->
                currentItem.selected = isChecked

                if (isChecked)
                    selectedCount++
                else
                    selectedCount--

            }
        }
    }

    fun setData(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }
}