package com.uninsubria.notec

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.uninsubria.notec.adapter.NoteAdapter
import com.uninsubria.notec.adapter.ViewPager
import com.uninsubria.notec.data.Folder
import com.uninsubria.notec.data.FolderViewModel
import com.uninsubria.notec.fragment.AddCategoryDialog
import com.uninsubria.notec.fragment.FoldersFragment
import com.uninsubria.notec.fragment.NotesFragment
import com.uninsubria.notec.fragment.OrderNotesDialog
import com.uninsubria.notec.util.Util
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), AddCategoryDialog.NoticeDialogListener, OrderNotesDialog.OrderNotesDialogListener {

    private lateinit var factory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var folderViewModel: FolderViewModel
    private val util = Util()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        folderViewModel = ViewModelProvider(this, factory).get(FolderViewModel::class.java)

        setSupportActionBar(myToolbar)
        setupViewPager()
        tab_layout.setupWithViewPager(viewPagerHome)
        setUpFAB()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, SearchableActivity::class.java)

        when (item.itemId) {
            R.id.mi_settings -> Toast.makeText(this, getString(R.string.settings), Toast.LENGTH_SHORT).show()
            R.id.mi_search -> startActivity(intent)
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val searchItem: MenuItem? = menu?.findItem(R.id.mi_search)
        val settingsItem: MenuItem? = menu?.findItem(R.id.mi_settings)
        val deleteItem: MenuItem? = menu?.findItem(R.id.mi_delete)
        val deleteAllItem: MenuItem? = menu?.findItem(R.id.mi_select_all)

        if(NoteAdapter.getCount() == 0) {
            searchItem?.isVisible= true
            settingsItem?.isVisible = true
            fab_button.show()
            fab_button_order.show()
            deleteItem?.isVisible = false
            deleteAllItem?.isVisible = false
        }
        else {
            searchItem?.isVisible = false
            settingsItem?.isVisible = false
            fab_button.hide()
            fab_button_order.hide()
            deleteItem?.isVisible = true
            deleteAllItem?.isVisible = true
        }
        return true
    }

    private fun setupViewPager() {

        val adapter = ViewPager(supportFragmentManager)
        adapter.addFragment(NotesFragment(), getString(R.string.all))
        adapter.addFragment(FoldersFragment(), getString(R.string.categories))
        viewPagerHome.adapter = adapter
    }

    private fun setUpFAB() {
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    fab_button.setImageResource(R.drawable.ic_new_note)
                    if(NoteAdapter.selectedCount == 0)
                        fab_button_order.show()
                }
                if (tab?.position == 1) {
                    fab_button.setImageResource(R.drawable.ic_category_fab)
                    fab_button_order.hide()
                }
            }
        })

        fab_button.setOnClickListener {
            val intent = Intent(this, CreateNoteActivity::class.java)
            if (tab_layout.selectedTabPosition == 0)
                startActivity(intent)
            else {
                AddCategoryDialog(this).show()
            }
        }

        fab_button_order.setOnClickListener {
            OrderNotesDialog(this).show()
        }
    }

    override fun onBackPressed() {

        if (NoteAdapter.getCount() == 0)
            finishAffinity()
        else {
            NoteAdapter.setCount(0)
            finish()
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    override fun onDialogPositiveClick(dialog: Dialog, category: String) {

        if (TextUtils.isEmpty(category))
            Toast.makeText(this, getString(R.string.empty_category), Toast.LENGTH_SHORT).show()
        else {
            folderViewModel.insert(Folder(util.lowerCaseNotFirst(category)))
            Toast.makeText(this, category + getString(R.string.category_added), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    override fun onDialogNegativeClick(dialog: Dialog) {
        dialog.dismiss()
    }

    override fun onOrderByDataFirst(dialog: Dialog) {
        Toast.makeText(this, "ordinata", Toast.LENGTH_SHORT).show()
    }

    override fun onOrderByDataLast(dialog: Dialog) {
        Toast.makeText(this, "ordinata", Toast.LENGTH_SHORT).show()
    }

    override fun onOrderByCategory(dialog: Dialog) {
        Toast.makeText(this, "ordinata", Toast.LENGTH_SHORT).show()
    }

    override fun onOrderByTitle(dialog: Dialog) {
        Toast.makeText(this, "ordinata", Toast.LENGTH_SHORT).show()
    }
}