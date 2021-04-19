package com.uninsubria.notec.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.uninsubria.notec.R
import com.uninsubria.notec.adapter.FolderAdapter
import com.uninsubria.notec.adapter.NoteAdapter
import com.uninsubria.notec.adapter.ViewpagerAdapter
import com.uninsubria.notec.database.model.Folder
import com.uninsubria.notec.database.viewmodel.FolderViewModel
import com.uninsubria.notec.fragment.*
import com.uninsubria.notec.ui.AddCategoryDialog
import com.uninsubria.notec.util.Util
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), AddCategoryDialog.NoticeDialogListener {

    private lateinit var factory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var folderViewModel: FolderViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var viewpagerAdapter :ViewpagerAdapter

    private val util = Util()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        folderViewModel = ViewModelProvider(this, factory).get(FolderViewModel::class.java)
        noteAdapter = NoteAdapter()
        viewpagerAdapter = ViewpagerAdapter(supportFragmentManager, lifecycle)

        setSupportActionBar(myToolbar)
        setupViewPager()
        TabLayoutMediator(tab_layout, viewPagerHome, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            when(position) {
                0 -> {tab.text = resources.getString(R.string.all)}
                1 -> {tab.text = resources.getString(R.string.categories)}
            }
        }).attach()
        setUpFab()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, SearchableActivity::class.java)

        when (item.itemId) {
            R.id.mi_settings -> { }
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
        val selectAllItem: MenuItem? = menu?.findItem(R.id.mi_select_all)


        if (NoteAdapter.getCount() > 0 || FolderAdapter.getCount() > 0)
            selectedLayout(searchItem, settingsItem, deleteItem, selectAllItem)
        else
           unselectedLayout(searchItem, settingsItem, deleteItem, selectAllItem)

        return true
    }

    private fun setupViewPager() {
        viewpagerAdapter.addFragment(NotesFragment())
        viewpagerAdapter.addFragment(FoldersFragment())
        viewPagerHome.adapter =  viewpagerAdapter
    }

    private fun setUpFab() {

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {

                if (tab?.position == 0) {
                    fab_button.hide()
                    fab_button.setImageResource(R.drawable.ic_new_note)
                    fab_button.show()
                    if (NoteAdapter.selectedCount == 0)
                        fab_button_order.show()
                }
                if (tab?.position == 1) {
                    fab_button.hide()
                    fab_button.setImageResource(R.drawable.ic_category_fab)
                    fab_button.show()
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

    private fun unselectedLayout(searchItem: MenuItem?, settingsItem: MenuItem?,
                               deleteItem: MenuItem?, selectAllItem: MenuItem?) {
        tab_layout.visibility = View.VISIBLE
        tv_selected.visibility = View.GONE
        viewPagerHome.isUserInputEnabled = true
        searchItem?.isVisible = true
        settingsItem?.isVisible = true
        fab_button.show()
        deleteItem?.isVisible = false
        selectAllItem?.isVisible = false
    }

    private fun selectedLayout(searchItem: MenuItem?, settingsItem: MenuItem?,
                                 deleteItem: MenuItem?, selectAllItem: MenuItem?) {
        tab_layout.visibility = View.GONE
        tv_selected.visibility = View.VISIBLE
        viewPagerHome.isUserInputEnabled = false
        searchItem?.isVisible = false
        settingsItem?.isVisible = false
        fab_button.hide()
        fab_button_order.hide()
        deleteItem?.isVisible = true
        selectAllItem?.isVisible = true
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
            Toast.makeText(this, util.lowerCaseNotFirst(category) + getString(R.string.category_added), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    override fun onDialogNegativeClick(dialog: Dialog) {
        dialog.dismiss()
    }

}