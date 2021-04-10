package com.uninsubria.notec.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.uninsubria.notec.R
import com.uninsubria.notec.util.Util
import kotlinx.android.synthetic.main.add_category_dialog.*

class CustomDialog(context : Context): Dialog(context) {

    private val listener : CustomDialog.NoticeDialogListener = context as NoticeDialogListener

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

        yesBtn.setOnClickListener {
            listener.onDialogPositiveClick(this, et_category.text.toString().trim())
        }
        noBtn.setOnClickListener {
            listener.onDialogNegativeClick(this)
        }
    }

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: Dialog, category: String)
        fun onDialogNegativeClick(dialog: Dialog)
    }

}
