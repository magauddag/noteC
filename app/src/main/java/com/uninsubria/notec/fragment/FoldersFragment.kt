package com.uninsubria.notec.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.uninsubria.notec.activities.FilteredNotesActivity
import com.uninsubria.notec.R
import com.uninsubria.notec.adapter.FolderAdapter
import com.uninsubria.notec.database.viewmodel.FolderViewModel
import kotlinx.android.synthetic.main.fragment_folders.*

class FoldersFragment : Fragment() {

    private lateinit var folderAdapter: FolderAdapter
    private lateinit var factory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var folderViewModel: FolderViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_folders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        folderAdapter = FolderAdapter()
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        folderViewModel = ViewModelProvider(this, factory).get(FolderViewModel::class.java)

        recyclerViewFolders.adapter = folderAdapter
        recyclerViewFolders.layoutManager = GridLayoutManager(this.context, 2)

        folderViewModel.getAllFolders().observe(viewLifecycleOwner, Observer {folders ->
            folderAdapter.setData(folders)
        })

        folderAdapter.onItemClick = {
            val intent = Intent(this.context, FilteredNotesActivity::class.java)
            intent.putExtra(FilteredNotesActivity.IDs.EXTRA_CATEGORY, it.category)
            startActivity(intent)
        }

        folderAdapter.onItemLongClick = {
            Toast.makeText(this.context, "Folder ${it.category} clicked long", Toast.LENGTH_SHORT).show()
        }
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