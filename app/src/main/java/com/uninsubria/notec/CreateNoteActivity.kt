package com.uninsubria.notec

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.util.Linkify
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
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
import com.uninsubria.notec.fragment.AddCategoryDialog
import com.uninsubria.notec.util.MyMovementMethod
import com.uninsubria.notec.util.Util
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.new_note_bottomsheet.*
import java.util.*


class CreateNoteActivity : AppCompatActivity(), AddCategoryDialog.NoticeDialogListener {

    object IntentId {
        const val EXTRA_ID = "com.uninsubria.notec.EXTRA_ID"
        const val EXTRA_TITLE = "com.uninsubria.notec.EXTRA_TITLE"
        const val EXTRA_BODY = "com.uninsubria.notec.EXTRA_BODY"
        const val EXTRA_CATEGORY = "com.uninsubria.notec.EXTRA_CATEGORY"
        const val CAMERA_REQUEST = 100
        const val GALLERY_REQUEST = 101
        const val CAMERA_PERM_CODE = 102
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
        addedImage.clipToOutline = true

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

                val selectedItem = parent?.getChildAt(0) as? TextView
                selectedItem?.setTextColor(resources.getColor(R.color.text_color_secondary))
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

        linearLayout1.setOnClickListener {

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, IntentId.CAMERA_REQUEST)
        }

        linearLayout2.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"

            if (galleryIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(galleryIntent, IntentId.GALLERY_REQUEST)
            }
        }

        linearLayout4.setOnClickListener {
            AddCategoryDialog(this).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            IntentId.CAMERA_REQUEST -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    imageLinearLayout.visibility = View.VISIBLE
                    val bitmap: Bitmap = data.extras?.get("data") as Bitmap
                    val cameraPhoto = Bitmap.createScaledBitmap(bitmap, 440, 900,true)
                    addedImage.setImageBitmap(cameraPhoto)
                }
            }
            IntentId.GALLERY_REQUEST -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    imageLinearLayout.visibility = View.VISIBLE
                    val imageUri = data.data
                    addedImage.setImageURI(imageUri)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
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









    /*@Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", *//* prefix *//*
                ".jpg", *//* suffix *//*
                storageDir *//* directory *//*
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath

        }
    }*/

    /*private fun dispatchTakePictureIntent() {

        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val photoFile = createImageFile()

        photoFile.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                    Objects.requireNonNull(applicationContext),
                    BuildConfig.APPLICATION_ID + ".provider",
                    it
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, IntentId.CAMERA_REQUEST)
        }
    }*/

    /*private fun dispatchTakePictureIntent() {

       *//* val shareIntent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            data = Uri.fromFile(File(currentPhotoPath))
        }*//*

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent

                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) { null }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        Objects.requireNonNull(applicationContext),
                        "com.uninsubria.notec.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    //takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivityForResult(takePictureIntent, IntentId.CAMERA_REQUEST)
                }
        }
    }*/



    /*private fun askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.CAMERA),
                IntentId.CAMERA_PERM_CODE
            )
        } else {
            dispatchTakePictureIntent()
        }
    }*/

    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if(requestCode == IntentId.CAMERA_PERM_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    /*private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }

    private fun scaleDownPic() {
        // Get the dimensions of the View
        val targetW: Int = addedImage.width
        val targetH: Int = addedImage.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            addedImage.setImageBitmap(bitmap)
        }
    }*/
}