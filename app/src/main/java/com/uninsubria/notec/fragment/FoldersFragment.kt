package com.uninsubria.notec.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.uninsubria.notec.R
import com.uninsubria.notec.adapter.FolderAdapter
import com.uninsubria.notec.data.FolderViewModel
import kotlinx.android.synthetic.main.fragment_folders.*

class FoldersFragment : Fragment() {

    val adapter = FolderAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewFolders.adapter = adapter
        recyclerViewFolders.layoutManager = GridLayoutManager(this.context, 2)

        val factory =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        val folderViewModel = ViewModelProvider(this, factory).get(FolderViewModel::class.java)

        folderViewModel.getAllFolders().observe(viewLifecycleOwner, Observer {folders ->
            adapter.setData(folders)
        })

        adapter.onItemClick = {
            Toast.makeText(this.context, "Folder ${it.category} clicked", Toast.LENGTH_SHORT).show()
        }

        adapter.onItemLongClick = {
            Toast.makeText(this.context, "Folder ${it.category} clicked long", Toast.LENGTH_SHORT).show()
        }
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