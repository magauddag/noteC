package com.uninsubria.notec.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.uninsubria.notec.activities.FilteredNotesActivity
import com.uninsubria.notec.R
import com.uninsubria.notec.adapter.FolderAdapter
import com.uninsubria.notec.database.model.Folder
import com.uninsubria.notec.database.viewmodel.FolderViewModel
import com.uninsubria.notec.database.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.fragment_folders.*
import kotlinx.android.synthetic.main.fragment_folders.emptyView
import kotlinx.android.synthetic.main.fragment_folders.loadingView

class FoldersFragment : Fragment() {

    private lateinit var folderAdapter: FolderAdapter
    private lateinit var factory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var folderViewModel: FolderViewModel
    private lateinit var noteViewModel: NoteViewModel

    private val toBeDeleted = HashMap<Int, Folder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_folders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        folderAdapter = FolderAdapter()
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        folderViewModel = ViewModelProvider(this, factory).get(FolderViewModel::class.java)
        noteViewModel = ViewModelProvider(this, factory).get(NoteViewModel::class.java)
        val selectedFoldersNumberText = activity?.findViewById<TextView>(R.id.tv_selected)

        displayFolders()
        setUpRecyclerView()

        folderAdapter.onItemClick = {
            val intent = Intent(this.context, FilteredNotesActivity::class.java)
            intent.putExtra(FilteredNotesActivity.IDs.EXTRA_CATEGORY, it.category)
            startActivity(intent)
        }

        folderAdapter.onItemSelected = { folder, folderSelected, position ->
            activity?.invalidateOptionsMenu()

            if (FolderAdapter.getCount() == 1)
                selectedFoldersNumberText?.text = FolderAdapter.getCount().toString() +
                        getString(R.string.single_item_selected)
            else
                selectedFoldersNumberText?.text = FolderAdapter.getCount().toString() +
                        getString(R.string.multiple_items_selected)

            if (folderSelected)
                toBeDeleted[position] = folder
            else
                toBeDeleted.remove(position)
        }

        folderAdapter.onItemLongClick = { folder, folderSelected, position ->
            activity?.invalidateOptionsMenu()

            if (FolderAdapter.getCount() == 1)
                selectedFoldersNumberText?.text = FolderAdapter.getCount().toString() +
                        getString(R.string.single_item_selected)
            else
                selectedFoldersNumberText?.text = FolderAdapter.getCount().toString() +
                        getString(R.string.multiple_items_selected)

            if (folderSelected)
                toBeDeleted[position] = folder
            else
                toBeDeleted.remove(position)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_search -> return false
            R.id.mi_settings -> return false
            R.id.mi_delete -> showDeleteFolderDialog()
        }
        return true
    }

    private fun showDeleteFolderDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.delete_folder_dialog)

        val yesBtn = dialog.findViewById(R.id.btn_confirm) as Button
        val noBtn = dialog.findViewById(R.id.button_cancel) as Button

        yesBtn.setOnClickListener {
            for (folder in toBeDeleted) {
                folderViewModel.delete(folder.value)
                noteViewModel.updateCategory(folder.value.category)
                folderAdapter.notifyItemRemoved(folder.key)
            }

            FolderAdapter.setCount(0)
            activity?.invalidateOptionsMenu()
            toBeDeleted.clear()
            dialog.dismiss()
        }

        noBtn.setOnClickListener {
            displayFolders()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun displayFolders() {
        folderViewModel.getAllFolders().observe(viewLifecycleOwner, Observer { folders ->
            folderAdapter.setData(folders)
        })
    }

    private fun setUpRecyclerView() {
        recyclerViewFolders.apply {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            emptyStateView = emptyView
            loadingStateView = loadingView
            adapter = folderAdapter
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
        }
    }

    private val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition
            val item = folderAdapter.folders[position]
            if(item.category == "Default") {
                displayFolders()
                Toast.makeText(this@FoldersFragment.context, getString(R.string.default_cant_be_deleted), Toast.LENGTH_SHORT).show()
                return
            }
            toBeDeleted[position] = item
            showDeleteFolderDialog()
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