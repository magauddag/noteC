package com.uninsubria.notec

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.util.Linkify
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.uninsubria.notec.data.Folder
import com.uninsubria.notec.data.FolderViewModel
import com.uninsubria.notec.data.Note
import com.uninsubria.notec.data.NoteViewModel
import com.uninsubria.notec.fragment.CustomDialog
import com.uninsubria.notec.util.MyMovementMethod
import com.uninsubria.notec.util.Util
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.new_note_bottomsheet.*
import kotlinx.android.synthetic.main.style_spinner.*


class CreateNoteActivity : AppCompatActivity(), CustomDialog.NoticeDialogListener {

    object IntentId {
        const val EXTRA_ID = "com.uninsubria.notec.EXTRA_ID"
        const val EXTRA_TITLE = "com.uninsubria.notec.EXTRA_TITLE"
        const val EXTRA_BODY = "com.uninsubria.notec.EXTRA_BODY"
        const val EXTRA_CATEGORY = "com.uninsubria.notec.EXTRA_CATEGORY"
    }

    private val util = Util()
    private lateinit var factory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var folderViewModel: FolderViewModel
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var categoryList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        noteViewModel = ViewModelProvider(this, factory).get(NoteViewModel::class.java)
        folderViewModel = ViewModelProvider(this, factory).get(FolderViewModel::class.java)
        categoryList = arrayListOf<String>(getString(R.string.choose_category))
        adapter = ArrayAdapter(this, R.layout.style_spinner, categoryList)

        setUpToolbar()
        setUpSpinner()
        setUpBottomSheet()
        manageLinksEditText()

        tv_date.text = util.getData()

        if (intent.hasExtra(IntentId.EXTRA_ID)) {
            et_title.setText(intent.getStringExtra(IntentId.EXTRA_TITLE))
            et_body.setText(intent.getStringExtra(IntentId.EXTRA_BODY))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.mi_save -> {
                if (intent.hasExtra(IntentId.EXTRA_ID))
                    updateItem()
                else
                    insertDataToDatabase()
            }
            R.id.mi_share -> shareNote()
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        val saveItem: MenuItem? = menu?.findItem(R.id.mi_save)
        val shareItem: MenuItem? = menu?.findItem(R.id.mi_share)

        if (!isValid(et_body.text.toString()) || spinner.selectedItem == getString(R.string.choose_category)) {
            saveItem?.isEnabled = false
            shareItem?.isEnabled = false
        }

        return super.onPrepareOptionsMenu(menu)
    }

    private fun insertDataToDatabase() {

        val intentDone = Intent(this, MainActivity::class.java)

        var noteTitle = et_title.text.toString().trim()
        val noteBody = et_body.text.toString().trim()
        val category = spinner.selectedItem.toString().trim()

        if (TextUtils.isEmpty(noteTitle)) {
            val i = noteBody.indexOf(' ')

            noteTitle = if (i > -1)
                noteBody.substring(0, i)
            else
                noteBody
        }

        val note = Note(
            0, 0, noteTitle, noteBody,
            util.getDataShort(), util.lowerCaseNotFirst(category), false
        )

        noteViewModel.insert(note)

        startActivity(intentDone)
        Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
    }

    private fun updateItem() {

        val noteID = intent.getIntExtra(IntentId.EXTRA_ID, 0)
        val noteTitle = et_title.text.toString().trim()
        val noteBody = et_body.text.toString().trim()
        val category = spinner.selectedItem.toString().trim()

        val updatedNote = Note(noteID, 0, noteTitle, noteBody, util.getDataShort(), category)

        noteViewModel.update(updatedNote)

        val intentDone = Intent(this, MainActivity::class.java)

        startActivity(intentDone)
        Toast.makeText(this, getString(R.string.update_message), Toast.LENGTH_SHORT).show()
    }

    private fun shareNote() {

        val note = "${et_title.text.trim()}\n" +
                "${et_body.text.trim()}\n\n" +
                "${tv_date.text}"

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, "${getString(R.string.all)}\n\n$note")
        intent.type = "text/plain, image/jpeg, image/png"
        startActivity(Intent.createChooser(intent, "Share To:"))
    }

    private fun isValid(noteBody: String): Boolean {
        return (!(TextUtils.isEmpty(noteBody)))
    }

    private fun setUpToolbar() {
        myToolbar2.title = ""
        setSupportActionBar(myToolbar2)

        myToolbar2.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpSpinner() {

        folderViewModel.getAllCategories().observe(this, Observer { categories ->
            adapter.addAll(categories)
        })

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                invalidateOptionsMenu()

                val selectedItem = parent?.getChildAt(0) as TextView
                selectedItem.setTextColor(resources.getColor(R.color.text_color_secondary))
            }
        }

        if (intent.hasExtra(IntentId.EXTRA_ID)) {
            spinner.setSelection(adapter.getPosition(intent.getStringExtra(IntentId.EXTRA_CATEGORY)))
        }
    }

    private fun setUpBottomSheet() {

        BottomSheetBehavior.from(new_note_bottomsheet).apply {
            linearLayoutImage.setOnClickListener {

                if (this.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    this.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    this.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
            val bottomSheetBehaviorCallback =

                object : BottomSheetBehavior.BottomSheetCallback() {

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        arrow.rotation = slideOffset * 180
                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                    }
                }
            this.setBottomSheetCallback(bottomSheetBehaviorCallback)
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        linearLayout4.setOnClickListener {
            CustomDialog(this).show()
        }
    }

    //make bottom sheet disappear when focus is lost
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        val mBottomSheetBehavior = BottomSheetBehavior.from(new_note_bottomsheet)

        if (event.action == MotionEvent.ACTION_DOWN) {
            if (mBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()
                new_note_bottomsheet.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()))
                    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        return super.dispatchTouchEvent(event)
    }

    //make links clickable and update menu item on edit text changes
    private fun manageLinksEditText() {
        et_body.linksClickable = false
        et_body.autoLinkMask = Linkify.WEB_URLS
        et_body.movementMethod = MyMovementMethod.getInstance()
        Linkify.addLinks(et_body, Linkify.WEB_URLS)

        et_body.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                invalidateOptionsMenu()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                invalidateOptionsMenu()
            }

            override fun afterTextChanged(s: Editable) {
                Linkify.addLinks(s, Linkify.WEB_URLS)
            }
        })
    }

    override fun onDialogPositiveClick(dialog: Dialog, category: String) {
        if (TextUtils.isEmpty(category))
            Toast.makeText(this, getString(R.string.empty_category), Toast.LENGTH_SHORT).show()
        else {
            adapter.clear()
            folderViewModel.insert(Folder(util.lowerCaseNotFirst(category)))
            Toast.makeText(this, category + getString(R.string.category_added), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    override fun onDialogNegativeClick(dialog: Dialog) {
        dialog.dismiss()
    }
}