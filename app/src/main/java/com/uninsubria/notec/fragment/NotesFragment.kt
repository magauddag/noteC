package com.uninsubria.notec.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.uninsubria.notec.activities.CreateNoteActivity
import com.uninsubria.notec.R
import com.uninsubria.notec.adapter.NoteAdapter
import com.uninsubria.notec.database.model.Note
import com.uninsubria.notec.database.viewmodel.NoteViewModel
import com.uninsubria.notec.util.SortType
import kotlinx.android.synthetic.main.fragment_notes.*

class NotesFragment : Fragment() {

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var factory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var noteViewModel: NoteViewModel

    private val toBeDeleted = HashMap<Int, Note>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteAdapter = NoteAdapter()
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        noteViewModel = ViewModelProvider(this, factory).get(NoteViewModel::class.java)
        val selectedNotesNumberText = activity?.findViewById<TextView>(R.id.tv_selected)

        displayNotes()
        setUpRecyclerView()

        noteAdapter.onItemClick = {
            val intent = Intent(this.context, CreateNoteActivity::class.java)

            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_ID, it.id)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_TITLE, it.title)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_BODY, it.body)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_CATEGORY, it.category)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_PATH, it.image)
            startActivity(intent)
        }

        noteAdapter.onItemSelected = {note, noteSelected, position ->
            activity?.invalidateOptionsMenu()

            if(NoteAdapter.getCount() == 1)
                selectedNotesNumberText?.text = NoteAdapter.getCount().toString() +
                        getString(R.string.single_item_selected)
            else
                selectedNotesNumberText?.text = NoteAdapter.getCount().toString() +
                        getString(R.string.multiple_items_selected)

            if(noteSelected)
                toBeDeleted[position] = note
            else
                toBeDeleted.remove(position)
        }

        noteAdapter.onItemLongClick = { note, noteSelected, position ->
            activity?.invalidateOptionsMenu()

            if(NoteAdapter.getCount() == 1)
                selectedNotesNumberText?.text = NoteAdapter.getCount().toString() +
                        getString(R.string.single_item_selected)
            else
                selectedNotesNumberText?.text = NoteAdapter.getCount().toString() +
                        getString(R.string.multiple_items_selected)

            if(noteSelected)
                toBeDeleted[position] = note
            else
                toBeDeleted.remove(position)
        }

        val fabOrderNotes = activity?.findViewById<FloatingActionButton>(R.id.fab_button_order)
        fabOrderNotes?.setOnClickListener {
            showOrderDialog()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.mi_search -> return false
            R.id.mi_settings -> return false
            R.id.mi_delete -> showDeleteNoteDialog()
            R.id.mi_select_all -> Toast.makeText(this.context, "selezionate", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private val itemTouchCallback = object : ItemTouchHelper.SimpleCallback (0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition
            val item = noteAdapter.notes[position]
            toBeDeleted[position] = item
            showDeleteNoteDialog()
        }
    }

    private fun setUpRecyclerView() {
        recyclerViewNotes.apply {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            emptyStateView = emptyView
            loadingStateView = loadingView
            adapter = noteAdapter
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
        }
    }

    private fun showOrderDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.order_notes_dialog)

        val descBtn = dialog.findViewById(R.id.linearLayoutLastOnTop) as LinearLayout
        val ascBtn = dialog.findViewById(R.id.linearLayoutFirstOnTop) as LinearLayout
        val categoryBtn = dialog.findViewById(R.id.linearLayoutCategory) as LinearLayout
        val titleBtn = dialog.findViewById(R.id.linearLayoutTitle) as LinearLayout

        ascBtn.setOnClickListener {
            noteViewModel.sortNotes(SortType.ASC)
            sortType = SortType.ASC
            noteAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        descBtn.setOnClickListener {
            noteViewModel.sortNotes(SortType.DESC)
            sortType = SortType.DESC
            noteAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        categoryBtn.setOnClickListener {
            noteViewModel.sortNotes(SortType.CATEGORY)
            sortType = SortType.CATEGORY
            noteAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        titleBtn.setOnClickListener {
            noteViewModel.sortNotes(SortType.TITLE)
            sortType = SortType.TITLE
            noteAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showDeleteNoteDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.delete_alert_dialog)

        val yesBtn = dialog.findViewById(R.id.btn_confirm) as Button
        val noBtn = dialog.findViewById(R.id.button_cancel) as Button

        yesBtn.setOnClickListener {
            for (note in toBeDeleted) {
                noteViewModel.delete(note.value)
                noteAdapter.notifyItemRemoved(note.key)
            }
            NoteAdapter.setCount(0)
            activity?.invalidateOptionsMenu()
            toBeDeleted.clear()
            dialog.dismiss()
        }

        noBtn.setOnClickListener {
            displayNotes()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun displayNotes() {

        when(sortType) {
            SortType.ASC -> noteViewModel.sortNotes(sortType)
            SortType.DESC -> noteViewModel.sortNotes(sortType)
            SortType.CATEGORY -> noteViewModel.sortNotes(sortType)
            SortType.TITLE -> noteViewModel.sortNotes(sortType)
        }

        noteViewModel.notes.observe(viewLifecycleOwner, Observer { notes ->
            noteAdapter.setData(notes)
        })
    }

    companion object {
        var sortType = SortType.ASC
        @JvmStatic
        fun newInstance() =
            NotesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }


}