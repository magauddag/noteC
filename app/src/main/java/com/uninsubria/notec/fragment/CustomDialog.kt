package com.uninsubria.notec.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.uninsubria.notec.R
import com.uninsubria.notec.data.FolderViewModel
import kotlinx.android.synthetic.main.add_category_dialog.*

class CustomDialog(context : Context): Dialog(context) {

    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.add_category_dialog)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val yesBtn = this.findViewById<Button>(R.id.btn_confirm)
        val noBtn = this.findViewById<Button>(R.id.button_cancel)
        val etCategory = et_category.text.toString()

        yesBtn.setOnClickListener {

            if (TextUtils.isEmpty(et_category.text.toString()))
                Toast.makeText(context, "Campo vuoto", Toast.LENGTH_SHORT).show()
            else {
                //folderViewModel.insert(Folder(et_category.text.toString()))
                Toast.makeText(context, et_category.text.toString(), Toast.LENGTH_SHORT).show()
                dismiss() }
        }

        noBtn.setOnClickListener {
            dismiss()
        }
    }
}
