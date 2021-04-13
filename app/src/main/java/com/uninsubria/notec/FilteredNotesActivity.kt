package com.uninsubria.notec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.uninsubria.notec.adapter.NoteAdapter
import com.uninsubria.notec.data.NoteViewModel
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.activity_filtered_notes.*

class FilteredNotesActivity : AppCompatActivity() {

    object IDs {
        const val EXTRA_CATEGORY = "com.uninsubria.notec.EXTRA_CATEGORY1"
    }

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var factory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var category : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtered_notes)

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
            startActivity(intent)
        }

        noteAdapter.onItemLongClick = {
            Toast.makeText(this, "LONG TEST", Toast.LENGTH_SHORT).show()
        }

        setUpToolbar()
        setUpRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.filtered_activity_menu, menu)
        return true
    }

    private fun setUpToolbar() {
        filteredToolbar.title = "$category notes"
        setSupportActionBar(filteredToolbar)

        filteredToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpRecyclerView() {

        filteredRecyclerView.adapter = noteAdapter
        filteredRecyclerView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)

        noteViewModel.getFilteredNotes(category).observe(this, Observer {notes ->
            noteAdapter.setData(notes)
        })

    }
}