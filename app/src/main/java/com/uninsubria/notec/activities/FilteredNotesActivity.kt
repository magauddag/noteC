package com.uninsubria.notec.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.uninsubria.notec.R
import com.uninsubria.notec.adapter.NoteAdapter
import com.uninsubria.notec.database.model.Note
import com.uninsubria.notec.database.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.activity_filtered.*
import kotlinx.android.synthetic.main.activity_filtered_notes.emptyView
import kotlinx.android.synthetic.main.activity_filtered_notes.empty_box_animation
import kotlinx.android.synthetic.main.activity_filtered_notes.filteredRecyclerView
import kotlinx.android.synthetic.main.activity_filtered_notes.filteredToolbar
import kotlinx.android.synthetic.main.activity_filtered_notes.loadingView


class FilteredNotesActivity : AppCompatActivity() {

    object IDs {
        const val EXTRA_CATEGORY = "com.uninsubria.notec.EXTRA_CATEGORY1"
        const val EXTRA_CATEGORY_FOR_CREATE_NOTE = "com.uninsubria.notec.EXTRA_CATEGORY2"
    }

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var factory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var category : String

    private val toBeDeleted = HashMap<Int, Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_filtered)

        noteAdapter = NoteAdapter()
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        noteViewModel = ViewModelProvider(this, factory).get(NoteViewModel::class.java)
        category = intent.getStringExtra(IDs.EXTRA_CATEGORY).toString()

        noteAdapter.onItemClick = {

            val intent = Intent(this, CreateNoteActivity::class.java)

            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_ID, it.id)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_TITLE, it.title)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_BODY, it.body)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_CATEGORY, it.category)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_PATH, it.image)
            startActivity(intent)
        }

        noteAdapter.onItemSelected = {note, noteSelected, position ->
            invalidateOptionsMenu()

            if(noteSelected)
                toBeDeleted[position] = note
            else
                toBeDeleted.remove(position)
        }

        noteAdapter.onItemLongClick = {note, noteSelected, position ->
            invalidateOptionsMenu()

            if(noteSelected)
                toBeDeleted[position] = note
            else
                toBeDeleted.remove(position)
        }

        setUpToolbar()
        setUpRecyclerView()
        setUpFab()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.filtered_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.mi_delete_filtered -> showDeleteNoteDialog()
        }
        return true
    }

    private fun showDeleteNoteDialog() {
        val dialog = Dialog(this)
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
            invalidateOptionsMenu()
            dialog.dismiss()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        val deleteItem: MenuItem? = menu?.findItem(R.id.mi_delete_filtered)

        if (NoteAdapter.getCount() == 0) {
            deleteItem?.isEnabled = false
            fab_button_filtered.show()
            filteredToolbar.setNavigationIcon(R.drawable.ic_back)
        }
        else {
            deleteItem?.isEnabled = true
            fab_button_filtered.hide()
            filteredToolbar.setNavigationIcon(R.drawable.ic_clear)
        }

        return true
    }

    private fun setUpToolbar() {
        filteredToolbar.title = "$category noteC"
        setSupportActionBar(filteredToolbar)

        filteredToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpRecyclerView() {

        empty_box_animation.setOnClickListener {
            empty_box_animation.playAnimation()
        }

        filteredRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            emptyStateView = emptyView
            loadingStateView = loadingView
            adapter = noteAdapter
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
        }

        noteViewModel.getFilteredNotes(category).observe(this, Observer {notes ->
            noteAdapter.setData(notes)
        })
    }

    private fun setUpFab() {
        fab_button_filtered.setOnClickListener {
            val intent = Intent(this, CreateNoteActivity::class.java)
            intent.putExtra(IDs.EXTRA_CATEGORY_FOR_CREATE_NOTE, category)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {

        if(NoteAdapter.getCount() > 0) {
            NoteAdapter.setCount(0)
            finish()
            startActivity(intent)
            overridePendingTransition(0, 0)
        }else
            super.onBackPressed()
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
}