package com.uninsubria.notec.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.uninsubria.notec.R
import com.uninsubria.notec.adapter.NoteAdapter
import com.uninsubria.notec.adapter.ViewPager
import com.uninsubria.notec.database.model.Folder
import com.uninsubria.notec.database.viewmodel.FolderViewModel
import com.uninsubria.notec.fragment.*
import com.uninsubria.notec.util.Util
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), AddCategoryDialog.NoticeDialogListener {

    private lateinit var factory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var folderViewModel: FolderViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var viewPager :ViewPager
    private val util = Util()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        folderViewModel = ViewModelProvider(this, factory).get(FolderViewModel::class.java)
        //noteViewModel = ViewModelProvider(this, factory).get(NoteViewModel::class.java)
        noteAdapter = NoteAdapter()
        viewPager = ViewPager(supportFragmentManager)

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
            R.id.mi_settings -> Toast.makeText(this, getString(
                R.string.settings
            ), Toast.LENGTH_SHORT).show()
            R.id.mi_search -> startActivity(intent)
            R.id.mi_delete -> return false
            R.id.mi_select_all -> return false
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val searchItem: MenuItem? = menu?.findItem(R.id.mi_search)
        val settingsItem: MenuItem? = menu?.findItem(R.id.mi_settings)
        val deleteItem: MenuItem? = menu?.findItem(R.id.mi_delete)
        val deleteAllItem: MenuItem? = menu?.findItem(R.id.mi_select_all)

        if (NoteAdapter.getCount() == 0) {
            searchItem?.isVisible = true
            settingsItem?.isVisible = true
            fab_button.show()
            fab_button_order.show()
            deleteItem?.isVisible = false
            deleteAllItem?.isVisible = false
        } else {
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

        viewPager.addFragment(NotesFragment(), getString(R.string.all))
        viewPager.addFragment(FoldersFragment(), getString(R.string.categories))
        viewPagerHome.adapter =  viewPager
    }

    private fun setUpFAB() {
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    fab_button.setImageResource(R.drawable.ic_new_note)
                    if (NoteAdapter.selectedCount == 0)
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
            folderViewModel.insert(
                Folder(
                    util.lowerCaseNotFirst(
                        category
                    )
                )
            )
            Toast.makeText(this, category + getString(R.string.category_added), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    override fun onDialogNegativeClick(dialog: Dialog) {
        dialog.dismiss()
    }

}