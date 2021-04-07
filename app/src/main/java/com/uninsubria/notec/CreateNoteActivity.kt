package com.uninsubria.notec

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.*
import android.text.util.Linkify
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.uninsubria.notec.data.Folder
import com.uninsubria.notec.data.FolderViewModel
import com.uninsubria.notec.data.Note
import com.uninsubria.notec.data.NoteViewModel
import com.uninsubria.notec.util.MyMovementMethod
import com.uninsubria.notec.util.Util
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.new_note_bottomsheet.*


class CreateNoteActivity : AppCompatActivity() {

    val util = Util()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        setUpToolbar()
        setUpSpinner()
        setUpBottomSheet()
        manageLinksEditText()

        tv_date.text = util.getData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.mi_save -> insertDataToDatabase()
            R.id.mi_share -> shareNote()
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        val saveItem: MenuItem? = menu?.findItem(R.id.mi_save)
        val shareItem: MenuItem? = menu?.findItem(R.id.mi_share)

        if (!isValid(et_body.text.toString())) {
            saveItem?.isEnabled = false
            shareItem?.isEnabled = false
        }

        return super.onPrepareOptionsMenu(menu)
    }

    private fun insertDataToDatabase() {

        val intent = Intent(this, MainActivity::class.java)

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

        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)

        val noteViewModel= ViewModelProvider(this, factory).get(NoteViewModel::class.java)
        val folderViewModel = ViewModelProvider(this, factory).get(FolderViewModel::class.java)

        val note = Note(0, 0, noteTitle, noteBody,
            util.getDataShort(), util.lowerCaseNotFirst(category))

        val folder = Folder(util.lowerCaseNotFirst(category))

        noteViewModel.insert(note)
        folderViewModel.insert(folder)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()

    }

    private fun shareNote() {

        val note = "${et_title.text.trim()}\n" +
                "${et_body.text.trim()}\n\n" +
                "${tv_date.text}"

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT,"${getString(R.string.all)}\n\n$note")
        intent.type="text/plain, image/jpeg, image/png"
        startActivity(Intent.createChooser(intent,"Share To:"))
    }


    private fun isValid(noteBody: String): Boolean {
        return (!(TextUtils.isEmpty(noteBody)))
    }

    private fun setUpToolbar () {
        myToolbar2.title = ""
        setSupportActionBar(myToolbar2)

        myToolbar2.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpSpinner() {

        var list = arrayListOf<String>("Fare la spesaaaa", "Libri", "Spesa", "Password")
        val arrayAdapter = ArrayAdapter<String>(this, R.layout.style_spinner, list)

        spinner.adapter = arrayAdapter

    }

    private fun setUpBottomSheet () {
        BottomSheetBehavior.from(new_note_bottomsheet).apply {
            linearLayoutImage.setOnClickListener {

                if (this.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    this.state = BottomSheetBehavior.STATE_EXPANDED
                }
                else {
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
            showDialog()
        }
    }

    //make bottom sheet and keyboard disappear when focus is lost
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
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
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

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_category_dialog)

        val yesBtn = dialog.findViewById(R.id.btn_confirm) as Button
        val noBtn = dialog.findViewById(R.id.button_cancel) as Button

        yesBtn.setOnClickListener {
            //TODO("Add confirm function")
            dialog.dismiss()
        }
        noBtn.setOnClickListener {
            //TODO("Add cancel function")
            dialog.dismiss() }
        dialog.show()

        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    }
}