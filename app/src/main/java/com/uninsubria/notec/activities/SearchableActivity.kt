package com.uninsubria.notec.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.uninsubria.notec.R
import com.uninsubria.notec.adapter.NoteAdapter
import com.uninsubria.notec.database.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.activity_searchable.*


class SearchableActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var factory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_searchable)

        adapter = NoteAdapter()
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        noteViewModel = ViewModelProvider(this, factory).get(NoteViewModel::class.java)


        adapter.onItemClick = {

            val intent = Intent(this, CreateNoteActivity::class.java)

            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_ID, it.id)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_TITLE, it.title)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_BODY, it.body)
            intent.putExtra(CreateNoteActivity.IntentId.EXTRA_CATEGORY, it.category)
            startActivity(intent)
        }

        setUpToolbar()
        setUpRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.searchable_menu, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.mi_searchable)
        val searchView = searchItem?.actionView as? SearchView
        setSearchViewEditTextBackgroundColor(this, searchView)
        searchView?.isSubmitButtonEnabled = false
        searchView?.inputType = EditorInfo.TYPE_CLASS_TEXT
        searchView?.inputType = EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES
        searchView?.queryHint = getString(R.string.search_note)
        searchView?.onActionViewExpanded()

        val searchPlateId = this.resources.getIdentifier("android:id/search_plate", null, null)
        val searchPlate = findViewById<EditText>(searchPlateId)

        val imm: InputMethodManager = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.showSoftInput(searchPlate, 0)

        searchView?.setOnQueryTextListener(this)

        return true
    }

    private fun setUpToolbar() {
        toolbarSearchable.title = ""
        setSupportActionBar(toolbarSearchable)
        toolbarSearchable.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpRecyclerView() {

        searchableRecyclerView.adapter = adapter
        searchableRecyclerView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        searchableRecyclerView.emptyStateView = emptyView
        searchableRecyclerView.loadingStateView = loadingView

        noteViewModel.getAllNotesAsc().observe(this, Observer { notes ->
            adapter.setData(notes)
        })
    }

    private fun setSearchViewEditTextBackgroundColor(context: Context, searchView: SearchView?) {

        val searchPlateId: Int = context.resources.getIdentifier("android:id/search_plate", null, null)
        val viewGroup = searchView?.findViewById<View>(searchPlateId) as ViewGroup
        viewGroup.background = ColorDrawable(android.graphics.Color.TRANSPARENT)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchDatabase(query)
        }
        return true
    }

    private fun searchDatabase(query: String) {
        val filterQuery = "%$query%"

        noteViewModel.searchDatabase(filterQuery).observe(this, Observer { notes ->
            adapter.setData(notes)
        })
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onBackPressed() {
        NoteAdapter.setCount(0)
        super.onBackPressed()
    }
}