package com.uninsubria.notec.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import com.uninsubria.notec.R

class OrderNotesDialog(context: Context) : Dialog(context) {

    init {
        setCancelable(true)
    }

    private val listener : OrderNotesDialogListener = context as OrderNotesDialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.order_notes_dialog)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dateFirst = this.findViewById<View>(R.id.linearLayoutDataFirst) as LinearLayout
        val dateLast= this.findViewById<View>(R.id.linearLayoutDataLast) as LinearLayout
        val categoryOrder= this.findViewById<View>(R.id.linearLayoutCategory) as LinearLayout
        val titleOrder = this.findViewById<View>(R.id.linearLayoutTitle) as LinearLayout

        dateFirst.setOnClickListener {
            listener.onOrderByDataFirst(this)
        }

        dateLast.setOnClickListener {
            listener.onOrderByDataLast(this)
        }

        categoryOrder.setOnClickListener {
            listener.onOrderByCategory(this)
        }

        titleOrder.setOnClickListener {
            listener.onOrderByTitle(this)
        }
    }

    interface OrderNotesDialogListener {

        fun onOrderByDataFirst(dialog: Dialog)
        fun onOrderByDataLast(dialog: Dialog)
        fun onOrderByCategory(dialog: Dialog)
        fun onOrderByTitle(dialog: Dialog)
    }
}