package com.uninsubria.notec.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.uninsubria.notec.R
import com.uninsubria.notec.adapter.NoteAdapter
import com.uninsubria.notec.data.Note
import com.uninsubria.notec.util.Util
import kotlinx.android.synthetic.main.fragment_notes.*


class NotesFragment : Fragment() {

    var notes = ArrayList<Note>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val util = Util()

        notes = util.generateCardList(15)
        /*var test = "This is pretty late in response, but for anyone else that is looking for this, you can do the following code to manually round the corners, " +
                "This is pretty late in response, but for anyone else that is looking for this, you can do the following code to manually round the corners"


        cards.add(Card(0, "Titolo", test, "Data", "Categoria"))
        cards.add(Card(R.drawable.a10f578, "Titolo", "ah boh non lo so io cosa scrivere ora vediamo ", "Data", "Categoria"))
        cards.add(Card(R.drawable.a10f578, "Titolo", test, "Data", "Categoria"))*/
        val adapter = NoteAdapter(notes)

        recyclerViewNotes.adapter = adapter
        recyclerViewNotes.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
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