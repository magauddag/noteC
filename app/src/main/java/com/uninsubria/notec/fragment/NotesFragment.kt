package com.uninsubria.notec.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.card.MaterialCardView
import com.uninsubria.notec.CreateNoteActivity
import com.uninsubria.notec.R
import com.uninsubria.notec.adapter.NoteAdapter
import com.uninsubria.notec.adapter.NoteAdapter.Companion.selectedCount
import com.uninsubria.notec.data.FolderViewModel
import com.uninsubria.notec.data.Note
import com.uninsubria.notec.data.NoteViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_note.*
import kotlinx.android.synthetic.main.card_note_no_image_material.*
import kotlinx.android.synthetic.main.fragment_notes.*

class NotesFragment : Fragment() {

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var factory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var noteViewModel: NoteViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteAdapter = NoteAdapter()

        recyclerViewNotes.adapter = noteAdapter
        recyclerViewNotes.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)

        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        noteViewModel= ViewModelProvider(this, factory).get(NoteViewModel::class.java)

        noteViewModel.getAllNotes().observe(viewLifecycleOwner, Observer { notes ->
            noteAdapter.setData(notes)
        })

        noteAdapter.onItemSelected = {
            //Toast.makeText(this.context, "${NoteAdapter.getCount()}", Toast.LENGTH_SHORT).show()
            Toast.makeText(this.context, "Nota ${it.title}", Toast.LENGTH_SHORT).show()
            activity?.invalidateOptionsMenu()
        }

        noteAdapter.onItemClick = {
            val intent = Intent(this.context, CreateNoteActivity::class.java)

            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_ID, it.id)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_TITLE, it.title)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_BODY, it.body)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_CATEGORY, it.category)
            startActivity(intent)
        }

        noteAdapter.onItemLongClick = {
            activity?.invalidateOptionsMenu()
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