package com.uninsubria.notec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.uninsubria.notec.adapter.ViewPager
import com.uninsubria.notec.fragment.FoldersFragment
import com.uninsubria.notec.fragment.NotesFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(myToolbar)
        setupViewPager()

        tab_layout.setupWithViewPager(viewPagerHome)
        fab_button.setOnClickListener { view ->
            val intent = Intent(this, CreateNoteActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.mi_settings -> Toast.makeText(this, getString(R.string.settings), Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun setupViewPager() {

        val adapter: ViewPager = ViewPager(supportFragmentManager)

        adapter.addFragment(NotesFragment(), getString(R.string.all))
        adapter.addFragment(FoldersFragment(), getString(R.string.categories))

        viewPagerHome.adapter = adapter
    }
}