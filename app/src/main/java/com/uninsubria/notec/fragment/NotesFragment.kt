package com.uninsubria.notec.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.uninsubria.notec.CreateNoteActivity
import com.uninsubria.notec.R
import com.uninsubria.notec.adapter.NoteAdapter
import com.uninsubria.notec.data.Note
import com.uninsubria.notec.data.NoteViewModel
import kotlinx.android.synthetic.main.fragment_notes.*

class NotesFragment : Fragment() {

    object updateId {
        const val EDIT_NOTE_REQUEST = 0
    }

    private val adapter = NoteAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewNotes.adapter = adapter
        recyclerViewNotes.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)

        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        val noteViewModel= ViewModelProvider(this, factory).get(NoteViewModel::class.java)

        noteViewModel.getAllNotes().observe(viewLifecycleOwner, Observer { notes ->
            adapter.setData(notes)
        })

        adapter.onItemClick = {

            val intent = Intent(this.context, CreateNoteActivity::class.java)

            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_ID, it.id)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_TITLE, it.title)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_BODY, it.body)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_CATEGORY, it.category)
            startActivity(intent)
        }

        adapter.onItemLongClick = {
            Toast.makeText(this.context, "LONG TEST", Toast.LENGTH_SHORT).show()
        }

    }

    companion object {

        @JvmStatic
        fun newInstance() =
            NotesFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}