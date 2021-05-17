package com.uninsubria.notec.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.uninsubria.notec.R
import com.uninsubria.notec.util.Util
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {

    val util = Util()
    private var photoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_image)

        chosenImage.clipToOutline = true
        chosenImage.setImageDrawable(null)
        setUpToolbar()

        if(intent.hasExtra(CreateNoteActivity.IntentId.EXTRA_PATH)) {
            photoPath = intent.getStringExtra(CreateNoteActivity.IntentId.EXTRA_PATH)
            chosenImage.setImageURI(Uri.parse(photoPath))
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.image_menu ,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.mi_delete_image -> {
                photoPath = null
                val resultIntent = Intent()
                resultIntent.putExtra("PATH", photoPath as String?)
                setResult(Activity.RESULT_OK, resultIntent)
                Toast.makeText(this, getString(R.string.image_deleted), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return true
    }

    private fun setUpToolbar() {
        myToolbarImageActivity.title = ""
        setSupportActionBar(myToolbarImageActivity)

        myToolbarImageActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}