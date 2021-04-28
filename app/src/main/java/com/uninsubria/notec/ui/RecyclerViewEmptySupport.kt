package com.uninsubria.notec.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.uninsubria.notec.util.RecyclerViewState

class RecyclerViewEmptySupport : RecyclerView {

    private var stateView: RecyclerViewState? =
        RecyclerViewState.LOADING
        set(value) {
            field = value
            setState()
        }

    var loadingStateView: View? = null
    var emptyStateView: View? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val dataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            onChangeState()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            onChangeState()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            onChangeState()
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(dataObserver)
        dataObserver.onChanged()
    }

    fun onChangeState() {
        if (adapter?.itemCount == 0) {
            emptyStateView?.visibility = View.VISIBLE
            loadingStateView?.visibility = View.GONE
            this@RecyclerViewEmptySupport.visibility = View.GONE
        } else {
            emptyStateView?.visibility = View.GONE
            loadingStateView?.visibility = View.VISIBLE
            this@RecyclerViewEmptySupport.visibility = View.VISIBLE
        }
    }

    private fun setState() {

        when (this.stateView) {

            RecyclerViewState.LOADING -> {
                loadingStateView?.visibility = View.VISIBLE
                this@RecyclerViewEmptySupport.visibility = View.GONE
                emptyStateView?.visibility = View.GONE
            }
            RecyclerViewState.NORMAL -> {
                loadingStateView?.visibility = View.GONE
                this@RecyclerViewEmptySupport.visibility = View.VISIBLE
                emptyStateView?.visibility = View.GONE
            }
            RecyclerViewState.EMPTY_STATE -> {
                loadingStateView?.visibility = View.GONE
                this@RecyclerViewEmptySupport.visibility = View.GONE
                emptyStateView?.visibility = View.VISIBLE
            }
        }
    }
}
