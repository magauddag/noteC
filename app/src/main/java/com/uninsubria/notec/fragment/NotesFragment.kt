package com.uninsubria.notec.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.uninsubria.notec.R
import com.uninsubria.notec.adapter.NoteAdapter
import com.uninsubria.notec.data.NoteViewModel
import kotlinx.android.synthetic.main.fragment_notes.*

class NotesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NoteAdapter()
        recyclerViewNotes.adapter = adapter
        recyclerViewNotes.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)

        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        val noteViewModel= ViewModelProvider(this, factory).get(NoteViewModel::class.java)


        noteViewModel.getAllNotes().observe(viewLifecycleOwner, Observer { notes ->
            adapter.setData(notes)
        })
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