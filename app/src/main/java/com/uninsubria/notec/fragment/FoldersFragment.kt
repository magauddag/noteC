package com.uninsubria.notec.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.uninsubria.notec.R
import com.uninsubria.notec.adapter.FolderAdapter
import com.uninsubria.notec.data.Folder
import com.uninsubria.notec.util.Util
import kotlinx.android.synthetic.main.fragment_folders.*

class FoldersFragment : Fragment() {

    var folders = ArrayList<Folder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val util = Util()
        folders = util.generateFolderList(15)

        val adapter = FolderAdapter(folders)
        recyclerViewFolders.adapter = adapter
        recyclerViewFolders.layoutManager = GridLayoutManager(this.context, 2)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_folders, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            FoldersFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}